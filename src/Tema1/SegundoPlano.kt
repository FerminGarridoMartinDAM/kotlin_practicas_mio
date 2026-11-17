/*
Este archivo demuestra cómo ejecutar un proceso hijo en segundo plano (background / asíncrono)
sin bloquear al proceso padre, descartando sus flujos de salida con Redirect.DISCARD y
reaccionando a su finalización mediante un callback asíncrono con onExit().
*/

/*
Puntos clave para estudio de SegundoPlano.kt:

- Diferencia clave entre Primer Plano (PrimerPlano.kt) y Segundo Plano (SegundoPlano.kt):
    - En Primer Plano: El padre lanza al hijo e inmediatamente hace `process.waitFor()` o lee su `inputStream` en un bucle bloqueante. El padre se queda quieto hasta que el hijo termina.
    - En Segundo Plano: El padre lanza al hijo y NO lo espera inmediatamente, sino que continúa ejecutando su propio código en paralelo (el bucle `repeat(3)`).

- ¿Por qué se usa `ProcessBuilder.Redirect.DISCARD` en procesos en segundo plano?
    Equivale a hacer `> /dev/null 2>&1` en Linux.
    Si un proceso hijo en segundo plano escribe mucho texto y el padre no lo lee (porque está ocupado haciendo otras cosas), el buffer de la tubería del sistema operativo se llena y el proceso hijo se queda congelado (bloqueado). Con `DISCARD`, el sistema operativo tira directamente la salida y evitamos bloqueos.

- ¿Cómo funciona `process.onExit().thenAccept { ... }`?
    `process.onExit()` devuelve un `CompletableFuture<Process>` (promesa asíncrona introducida en Java 9).
    Con `.thenAccept { p -> ... }`, registramos una función (callback) que se ejecutará automáticamente en un hilo separado en el instante exacto en que el hijo termine, sin detener al hilo principal del padre.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

fun main() {
    // Configura un subproceso que tardará 5 segundos en terminar ("sleep 5")
    val process = ProcessBuilder("bash","-c","sleep 5")
        // Comentario original de la profesora:
        // Descarta todo lo que el hijo imprima por stdout para no llenar buffers de memoria
        .redirectOutput(ProcessBuilder.Redirect.DISCARD) // Descartar la salida
        // Comentario original de la profesora:
        // Descarta todo lo que el hijo imprima por stderr
        .redirectError(ProcessBuilder.Redirect.DISCARD)  // Descartar los errores
        // Inicia el proceso hijo en el sistema operativo y devuelve el control inmediatamente al padre
        .start()

    // Imprime el PID del proceso hijo recién arrancado
    println("Proceso iniciado en segundo plano con PID: ${process.pid()}")

    // Comentario original de la profesora:
    // Registrar qué hacer cuando termine, sin bloquear al padre
    // onExit() vigila al proceso en segundo plano; cuando pasen los 5 segundos del sleep,
    // ejecutará automáticamente este bloque lambda imprimiendo su PID y su código de salida (exitValue())
    process.onExit().thenAccept { p ->
        println("El proceso ${p.pid()} ha terminado con código ${p.exitValue()}")
    }

    // Comentario original de la profesora:
    // Mientras tanto, el programa padre sigue trabajando
    // Este bucle dura 3 repeticiones x 3 segundos = 9 segundos en total.
    // Como el hijo solo dura 5 segundos, veremos el mensaje de "El proceso X ha terminado..."
    // intercalado por consola en mitad del trabajo del padre (entre la iteración 1 y la 2).
    repeat(3) { i ->
        println("El padre sigue trabajando... ($i)")
        Thread.sleep(3000)
    }

    // Comentario original de la profesora:
    // Si el trabajo del padre fuera más corto que el del hijo, la JVM se cerraría antes de que saltara el onExit().
    // Este waitFor() final garantiza que main() no se cierre si el hijo aún siguiera vivo.
    process.waitFor() // Solo para que el ejemplo no termine antes que el hijo
}