/*
Este archivo aplica la plantilla completa de ProcessBuilder para ejecutar el comando 'grep'
buscando una cadena de texto directamente sobre un fichero ubicado en un directorio específico.
*/

/*
Puntos clave para estudio de ProcessGrep.kt:

- Dos formas de usar 'grep' vistas en el tema:
    1. En Pipes.kt: Ejecutábamos `grep kotlin` sin indicar archivo; grep leía de su entrada estándar (stdin) lo que Kotlin le enviaba por process.outputStream.
    2. En ProcessGrep.kt: Ejecutamos `grep cadena_a_buscar archivo.txt`; grep abre directamente `archivo.txt` dentro del directorio configurado con `.directory(...)` y no espera nada por stdin.

- Códigos de salida (exitCode) importantes de 'grep' para examen:
    - 0: Encontró al menos una línea con la palabra buscada.
    - 1: El archivo existe y se leyó bien, pero NO encontró ninguna coincidencia.
    - 2: Ocurrió un error (por ejemplo, `archivo.txt` no existe en ese directorio). Como tenemos `redirectErrorStream(true)`, veremos el mensaje `grep: archivo.txt: No such file or directory` impreso por el bucle while.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones para manejo de rutas (File), flujos de lectura (InputStreamReader, BufferedReader) y creación de procesos
import java.io.File
import java.io.InputStreamReader
import java.io.BufferedReader
import java.lang.ProcessBuilder
// Importación de TimeUnit (incluida en el archivo original; útil cuando se usa process.waitFor(tiempo, TimeUnit.SECONDS))
import java.util.concurrent.TimeUnit

fun main() {
    // Comentario original de la profesora:
    // Crear un proceso para ejecutar el comando 'grep'
    // Grep sirve para buscar una cadena de texto en un archivo
    // Sintaxis: grep <patrón_a_buscar> <nombre_del_archivo>
    val processBuilder = ProcessBuilder("bash","-c","grep cadena_a_buscar archivo.txt")

    // Fija la carpeta donde bash buscará el fichero 'archivo.txt'
    processBuilder.directory(File("C:\\Users\\Usuario\\Desktop\\2DAM-AS-2627\\Kotlin_Practicas\\src\\Tema1"))

    // Comentario original de la profesora:
    // Unifica la salida de error con la estándar (muy útil aquí por si 'archivo.txt' no existe)
    processBuilder.redirectErrorStream(true) // Redirigir errores al flujo de salida

    try {
        // Comentario original de la profesora:
        val process = processBuilder.start() // Iniciar el proceso

        // Comentario original de la profesora:
        // Leer la salida del proceso
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        // Imprime únicamente las líneas de 'archivo.txt' que contengan "cadena_a_buscar" (o el mensaje de error si el archivo no existe)
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        // Comentario original de la profesora:
        // Esperar a que el proceso termine
        val exitCode = process.waitFor()
        println("Proceso terminado con código de salida: $exitCode")
    } catch (e: Exception) {
        // Captura fallos al lanzar el proceso (por ejemplo, si el directorio indicado en .directory() no existe en el PC)
        e.printStackTrace()
    }
}