/*
Este archivo combina la programación multihilo (kotlin.concurrent.thread) con la programación
multiproceso (ProcessBuilder) para lanzar varios procesos pesados o lentos en paralelo
y esperar a que todos terminen utilizando join().
*/

/*
Puntos clave para estudio de 006.kt:

* ¿Por qué envolver ProcessBuilder dentro de un hilo (thread)?
    Aunque ProcessBuilder.start() arranca el proceso de forma asíncrona, process.waitFor() es bloqueante.
    Si hiciéramos un bucle normal en el main llamando a start() y waitFor(), no se lanzaría el segundo proceso hasta que terminara el primero (ejecución secuencial: 1s + 2s + 3s = 6 segundos).
    Al meter start() + waitFor() dentro de un bloque thread { ... }, cada hilo secundario se queda bloqueado en su propio waitFor(), permitiendo que los 3 procesos se ejecuten simultáneamente en el Sistema Operativo (tiempo total = 3 segundos).

* Diferencia entre waitFor() y join():
    - process.waitFor() -> El hilo de Kotlin espera a que termine un PROCESO externo del Sistema Operativo.
    - hilo.join()       -> El hilo principal (main) espera a que termine otro HILO dentro de la misma JVM.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa la función 'thread' de Kotlin, que crea e inicia automáticamente un objeto Thread de Java
import kotlin.concurrent.thread

fun main() {
    // Lista inmutable con los segundos que debe durar cada proceso (como Strings)
    val comandos = listOf("1", "2", "3")

    // comandos.map { ... }: Transforma cada elemento de la lista ("1", "2", "3") en un hilo (Thread) ya en ejecución.
    // La variable 'hilos' resultante es de tipo List<Thread>.
    val hilos = comandos.map { segundos ->
        // Crea y arranca un nuevo hilo concurrente para gestionar este subproceso
        thread {
            // El hilo lanza su proceso correspondiente.
            // (Nota: igual que en 004.kt, en bash -c debería escribirse ProcessBuilder("bash", "-c", "sleep $segundos") para que tome el argumento).
            val process = ProcessBuilder("bash","-c","sleep", segundos).start()

            // Bloquea ÚNICAMENTE a este hilo secundario hasta que su proceso 'sleep' termine
            process.waitFor()

            // Informa por consola qué proceso acaba de terminar
            println("Proceso sleep $segundos terminado")
        }
    }

    // Recorre la lista de los 3 hilos lanzados y llama a .join() en cada uno.
    // Esto obliga al hilo principal (main) a esperar hasta que los 3 hilos hayan acabado antes de finalizar el programa.
    hilos.forEach { it.join() }
}