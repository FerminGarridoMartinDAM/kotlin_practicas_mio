/*
Este archivo implementa el patrón de diseño de concurrencia "Monitor" utilizando
ReentrantLock y variables de condición (Condition) para coordinar tanto la exclusión mutua
como la espera activa/notificación entre hilos.
*/

/*
Puntos clave para estudio de Monitores.kt:

- ¿Qué diferencia a un Monitor de un simple Mutex?
    Un Mutex solo impide que dos hilos entren a la vez (exclusión mutua).
    Un Monitor, además de exclusión mutua (ReentrantLock), encapsula el estado del recurso (recursoDisponible) y permite que un hilo que ya entró pero no puede continuar se duerma liberando la llave temporalmente (condition.await()) y sea despertado cuando otro hilo cambie el estado (condition.signal()).

- Pregunta clásica de examen: ¿Por qué se usa `while (!recursoDisponible)` y NO `if (!recursoDisponible)` antes de `condition.await()`?
    1. Despertares espurios (Spurious Wakeups): El sistema operativo puede despertar un hilo en espera sin que nadie haya llamado a signal().
    2. Competencia entre varios hilos: Si otro hilo se cuela antes de que el hilo despertado recupere el lock, el recurso podría volver a estar ocupado. El bucle `while` obliga a volver a comprobar la condición antes de seguir.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa Condition: permite suspender hilos (await) y despertarlos (signal / signalAll) dentro de un ReentrantLock
import java.util.concurrent.locks.Condition
// Importa ReentrantLock: cerrojo de exclusión mutua explícito más flexible que el bloque synchronized tradicional
import java.util.concurrent.locks.ReentrantLock
// Importa thread para crear hilos fácilmente
import kotlin.concurrent.thread
// Importa la extensión withLock, que adquiere el cerrojo al entrar al bloque y lo libera automáticamente en un finally al salir
import kotlin.concurrent.withLock

/*
Comentario original de la profesora:
En este ejemplo, la clase Monitor encapsula un recurso compartido y
 proporciona métodos para acceder a él de manera segura.
 El primero que llama a accederRecurso() lo ocupa;
 el segundo se queda esperando en condition.await()
 (lo que libera temporalmente el lock)
 hasta que el primero llama a liberarRecurso()
 y le avisa con condition.signal().
 */

class Monitor {
    // Cerrojo interno que garantiza que solo un hilo pueda ejecutar código dentro de accederRecurso o liberarRecurso a la vez
    private val lock = ReentrantLock()
    // Variable de condición asociada a este cerrojo para gestionar la cola de hilos dormidos esperando el recurso
    private val condition: Condition = lock.newCondition()
    // Variable de estado (predicado) que indica si el recurso está libre (true) u ocupado (false)
    private var recursoDisponible = true // Al principio el recurso está libre

    fun accederRecurso() {
        // Adquiere el cerrojo antes de comprobar o modificar 'recursoDisponible'
        lock.withLock {
            // Mientras el recurso esté ocupado por otro hilo, este hilo no puede avanzar
            while (!recursoDisponible) {
                // condition.await() hace 3 cosas de forma atómica:
                // 1. Libera temporalmente el 'lock' para que el otro hilo pueda entrar a 'liberarRecurso()'.
                // 2. Duerme al hilo actual hasta que alguien llame a condition.signal().
                // 3. Antes de devolver el control, vuelve a adquirir el 'lock'.
                condition.await()
            }
            // Cuando sale del while, tenemos el lock y sabemos seguro que el recurso estaba libre
            println("Accediendo al recurso compartido")
            // Marcamos el recurso como ocupado para que cualquier otro hilo que entre después tenga que esperar
            recursoDisponible = false
        } // Al salir de withLock, se libera el cerrojo físico (pero recursoDisponible sigue en false)
    }

    fun liberarRecurso() {
        // Adquiere el cerrojo para modificar el estado de forma segura
        lock.withLock {
            println("Liberando el recurso compartido")
            // Cambia el estado de nuevo a libre
            recursoDisponible = true
            // Despierta a un hilo que estuviera dormido en condition.await() avisándole de que ya puede reintentar
            condition.signal()
        } // Libera el cerrojo para que el hilo recién despertado pueda adquirirlo y salir de su await()
    }
}

fun main() {
    // Instancia única del monitor que compartirán ambos hilos
    val monitor = Monitor()

    // Crea una lista de 2 hilos que intentarán usar el mismo monitor simultáneamente
    val tareas = (1..2).map {
        thread {
            // Solicita permiso al monitor; el segundo hilo quedará bloqueado aquí hasta que el primero libere
            monitor.accederRecurso()
            // Simula durante 1 segundo que está utilizando el recurso exclusivo
            Thread.sleep(1000)
            // Devuelve el recurso y despierta al siguiente hilo en espera
            monitor.liberarRecurso()
        }
    }
    // El hilo principal (main) espera a que ambos hilos terminen su ejecución antes de finalizar el programa
    tareas.forEach { it.join() }
}