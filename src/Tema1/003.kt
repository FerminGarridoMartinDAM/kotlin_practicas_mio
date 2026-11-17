/*
Este archivo aborda la gestión de errores en procesos hijos y demuestra la diferencia
fundamental entre un fallo al lanzar el ejecutable desde la JVM (IOException)
y un fallo reportado por el intérprete de comandos a través del canal de error estándar (errorStream).
*/

/*
Puntos clave para estudio de 003.kt:

* Diferencia crítica de examen entre IOException y errorStream:
    1. Si haces ProcessBuilder("comando_inexistente").start() directamente:
       El sistema operativo le dice a la JVM que no existe ningún archivo binario con ese nombre, y start() aborta lanzando una IOException en Kotlin. No llega a crearse ningún objeto Process ni hay errorStream que leer.
    2. Si haces ProcessBuilder("bash", "-c", "comando_inexistente").start():
       El binario '/bin/bash' SÍ existe en el sistema operativo, por lo que el proceso arranca sin lanzar IOException. Cuando bash intenta buscar "comando_inexistente" y no lo encuentra, es el propio bash quien escribe el mensaje de error en su salida de error estándar (process.errorStream) y termina con código de salida 127.

* Función de redirectErrorStream(Boolean):
    - En false (valor por defecto): Mantiene separados los canales. La salida normal se lee en process.inputStream y los errores en process.errorStream.
    - En true: Fusiona ambos canales. Todos los mensajes (normales y de error) se leen juntos desde process.inputStream, y process.errorStream queda vacío.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Comentario original del profesor:
// Lanzamos el comando a través del intérprete porque,
// si se pasa directamente a ProcessBuilder, start()
// lanza una IOException (el ejecutable no existe)
// y no llega a haber ninguna salida de error que leer.
// En cambio, el intérprete sí arranca y es él quien escribe
// el error en su salida de error.


import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    // Configuramos ProcessBuilder para que arranque 'bash' y este intente ejecutar "comando_inexistente".
    // Como el ejecutable 'bash' sí existe en el sistema operativo, pb.start() tendrá éxito.
    val pb = ProcessBuilder("bash", "-c", "comando_inexistente")

    // redirectErrorStream(false): Indica que NO queremos mezclar la salida de error (stderr) con la salida estándar (stdout).
    // De este modo, stdout irá por process.inputStream y stderr irá por process.errorStream.
    pb.redirectErrorStream(false)

    // Arranca el proceso hijo en el sistema operativo
    val process = pb.start()

    // Lector conectado a la salida estándar del hijo (descriptor de fichero 1 en Linux: stdout)
    val stdOut = BufferedReader(InputStreamReader(process.inputStream))

    // Lector conectado a la salida de error del hijo (descriptor de fichero 2 en Linux: stderr)
    val stdErr = BufferedReader(InputStreamReader(process.errorStream))

    // Intentamos leer la salida normal; como el comando falló, este flujo estará vacío (no imprimirá líneas)
    println("Salida estándar:")
    stdOut.lines().forEach { println(it) }

    // Leemos la salida de error; aquí bash habrá escrito: "bash: comando_inexistente: command not found"
    println("Salida de error:")
    stdErr.lines().forEach { println(it) }
}