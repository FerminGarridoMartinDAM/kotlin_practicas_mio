/*
Este archivo explica el uso de Semáforos (java.util.concurrent.Semaphore) para controlar
el acceso concurrente de múltiples hilos a un recurso compartido mediante un sistema de permisos.
*/

/*
Puntos clave para estudio de Semaforo.kt:

- Diferencia de examen entre un Mutex (`ReentrantLock`) y un Semáforo (`Semaphore`):
    1. Número de hilos permitidos:
       - Un `Mutex` (`ReentrantLock`) solo deja pasar a **1 hilo** a la vez.
       - Un `Semaphore(N)` tiene un contador interno de permisos (`permits`). Permite que entren hasta **N hilos simultáneamente** a la sección crítica.
       - Caso particular (Semáforo Binario): Si lo creas con `Semaphore(1)` (como en este archivo), solo hay 1 permiso disponible, por lo que se comporta en la práctica igual que un Mutex.
    2. Propiedad del bloqueo:
       - En un `Mutex`, solo el hilo que hizo `lock()` tiene derecho a hacer `unlock()`.
       - En un `Semaphore`, no existe concepto de "propietario": cualquier hilo podría llamar a `release()` para incrementar los permisos.

- Funcionamiento interno de `acquire()` y `release()`:
    - `semaphore.acquire()`: Si el contador de permisos es `> 0`, resta 1 al contador y el hilo entra. Si el contador es `0`, el hilo se bloquea esperando hasta que alguien libere un permiso.
    - `semaphore.release()`: Suma 1 al contador de permisos y despierta a un hilo que estuviera esperando en `acquire()`.
    - Regla de oro: `semaphore.release()` SIEMPRE debe ir dentro de un bloque `finally` para que el permiso se devuelva aunque salte una excepción dentro del `try`.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa la clase Semaphore del paquete de utilidades de concurrencia de Java
import java.util.concurrent.Semaphore
// Importa la función helper thread para crear hilos en Kotlin
import kotlin.concurrent.thread

fun main() {
    // Comentario original de la profesora:
    // Crea un semáforo con 1 único permiso disponible (Semáforo binario = exclusión mutua)
    // Si pusiéramos Semaphore(3), podrían entrar hasta 3 hilos a la vez.
    val semaphore = Semaphore(1) // Semáforo binario

    // Creamos e iniciamos 2 hilos concurrentes con identificadores 1 y 2
    val tareas = (1..2).map { id ->
        thread {
            // Solicita 1 permiso al semáforo:
            // - El primer hilo que llega ve el contador en 1 -> lo deja en 0 y entra al bloque try.
            // - El segundo hilo que llega ve el contador en 0 -> se queda bloqueado aquí esperando.
            semaphore.acquire()
            try {
                // --- INICIO DE LA SECCIÓN CRÍTICA ---
                println("Proceso $id: Accediendo al recurso compartido")
                // Simula el uso del recurso durante 1 segundo
                Thread.sleep(1000)
                println("Proceso $id: Liberando el recurso compartido")
                // --- FIN DE LA SECCIÓN CRÍTICA ---
            } finally {
                // Comentario original de la profesora:
                // Devuelve el permiso al semáforo (el contador vuelve a 1) y despierta al hilo que esperaba en acquire()
                semaphore.release() // Liberar siempre, aunque haya una excepción
            }
        }
    }
    // El hilo principal espera a que ambos hilos terminen antes de finalizar el programa
    tareas.forEach { it.join() }
}