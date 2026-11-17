/*
Este archivo ejecuta el comando 'ps' (Process Status) de Linux/bash utilizando la estructura
completa con redirección de errores, control de excepciones y espera del código de salida.
*/

/*
Puntos clave para estudio de ProcessPs.kt:

- Comparativa entre `005.kt` y `ProcessPs.kt`:
    Ambos ejecutan exactamente el mismo comando (`bash -c ps`), pero `ProcessPs.kt` añade las tres buenas prácticas obligatorias en proyectos reales o exámenes:
    1. `redirectErrorStream(true)` para no perder mensajes de error.
    2. Bloque `try-catch` para evitar que el programa colapse ante una `IOException`.
    3. `process.waitFor()` para sincronizar el cierre del proceso hijo y verificar su código de retorno (`exitCode`).

- ¿Qué columnas muestra el comando `ps` simple por pantalla?
    - `PID`: Identificador único de cada proceso.
    - `TTY`: Terminal asociada al proceso.
    - `TIME`: Tiempo total de CPU consumido por el proceso.
    - `CMD`: Nombre del comando que se está ejecutando (verás `bash` y el propio `ps`).
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones estándar para entrada/salida y gestión de procesos
import java.io.File
import java.io.InputStreamReader
import java.io.BufferedReader
import java.lang.ProcessBuilder
import java.util.concurrent.TimeUnit

fun main() {
    // Comentario original de la profesora:
    // Crear un proceso para ejecutar el comando 'ps'
    // 'ps' sin argumentos muestra los procesos activos asociados a la sesión de terminal actual
    val processBuilder = ProcessBuilder("bash","-c","ps")

    // Comentario original de la profesora:
    // Fusiona stderr con stdout para leer cualquier posible error desde el mismo inputStream
    processBuilder.redirectErrorStream(true) // Redirigir errores al flujo de salida

    try {
        // Comentario original de la profesora:
        val process = processBuilder.start() // Iniciar el proceso

        // Comentario original de la profesora:
        // Leer la salida del proceso
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        // Recorre e imprime la cabecera ("PID TTY TIME CMD") y cada fila de procesos devuelta por 'ps'
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        // Comentario original de la profesora:
        // Esperar a que el proceso termine
        // Espera a que 'ps' finalice y devuelve su código de salida (0 si todo fue bien)
        val exitCode = process.waitFor()
        println("Proceso terminado con código de salida: $exitCode")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}