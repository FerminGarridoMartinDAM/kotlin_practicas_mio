/*
Este archivo demuestra el uso directo de un Mutex (Mutual Exclusion / Exclusión Mutua)
mediante ReentrantLock para proteger una sección crítica entre varios hilos.
*/

/*
Puntos clave para estudio de Mutexes.kt:

- Concepto de Sección Crítica y Mutex:
    La sección crítica es el fragmento de código donde se accede a un recurso compartido (variables, ficheros, hardware).
    El Mutex actúa como el cerrojo de una puerta con una única llave: el primer hilo que llega cierra con llave y entra; el segundo se queda bloqueado en la puerta hasta que el primero sale y devuelve la llave.

- ¿Por qué se llama ReentrantLock (Cerrojo Reentrante)?
    Porque si un hilo que YA posee el cerrojo intenta volver a bloquearlo (por ejemplo, llamando a una función recursiva o a otro método protegido por el mismo lock), no se bloquea a sí mismo (evita un autodeadlock), sino que incrementa un contador interno de propietarios.

- Ventaja de `lock.withLock { ... }` frente a `lock.lock()` y `lock.unlock()`:
    Si usas `lock.lock()` manualmente y ocurre una excepción dentro de la sección crítica antes de llegar a `lock.unlock()`, el cerrojo jamás se libera y todos los demás hilos quedan colgados para siempre. `withLock` mete automáticamente el `unlock()` dentro de un bloque `finally`.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa ReentrantLock: implementación estándar de un Mutex en el paquete java.util.concurrent.locks
import java.util.concurrent.locks.ReentrantLock
// Importa la función thread para lanzar hilos concurrentes
import kotlin.concurrent.thread
// Importa la función de extensión withLock para gestionar la adquisición y liberación segura del cerrojo
import kotlin.concurrent.withLock

fun main() {
    // Instancia única del cerrojo (Mutex) que deben compartir todos los hilos que quieran sincronizarse
    val lock = ReentrantLock()

    // Recorre el rango del 1 al 2 creando dos hilos concurrentes e identificándolos con 'id' (1 y 2)
    val tareas = (1..2).map { id ->
        thread {
            // Comentario original de la profesora:
            // withLock bloquea el mutex y lo desbloquea al salir del bloque
            lock.withLock {
                // --- INICIO DE LA SECCIÓN CRÍTICA ---
                // Solo un hilo puede estar ejecutando estas tres líneas en un instante dado
                println("Proceso $id: Accediendo al recurso compartido")
                // El hilo retiene el cerrojo durante 1 segundo; el otro hilo está pausado esperando en lock.withLock
                Thread.sleep(1000)
                println("Proceso $id: Liberando el recurso compartido")
                // --- FIN DE LA SECCIÓN CRÍTICA ---
            } // Aquí se ejecuta internamente lock.unlock() dentro de un bloque finally, cediendo el paso al otro hilo
        }
    }
    // Espera a que los dos hilos hayan terminado antes de cerrar el hilo main
    tareas.forEach { it.join() }
}