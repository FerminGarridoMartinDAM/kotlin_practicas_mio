/*
Este archivo muestra cómo comunicarse bidireccionalmente desde el proceso padre (Kotlin)
con un subproceso hijo (grep) a través de tuberías anónimas (pipes), enviándole datos
por su entrada estándar (outputStream) y recogiendo el resultado filtrado por su salida estándar (inputStream).
*/

/*
Puntos clave para estudio de Pipes.kt:

- El flujo completo de datos Padre <-> Hijo:
    1. Padre escribe en `process.outputStream` -> entra por el `stdin` de `grep`.
    2. `grep` filtra solo las líneas que contienen la palabra "kotlin".
    3. `grep` escribe el resultado en su `stdout` -> el Padre lo lee desde `process.inputStream`.

- ERROR TÍPICO DE EXAMEN (Bloqueo infinito por no cerrar outputStream):
    Comandos como `grep`, `cat`, `sort` o `wc` leen de su entrada estándar hasta que encuentran la marca de fin de fichero (EOF).
    Si olvidas hacer `writer.close()`, la tubería de entrada queda abierta, `grep` se queda esperando eternamente a que le envíes más líneas, y tu bucle de lectura se cuelga para siempre.

- Sintaxis idiomática de lectura en Kotlin: `while (reader.readLine().also { line = it } != null)`:
    En Java se escribe `while ((line = reader.readLine()) != null)`. En Kotlin las asignaciones (`=`) no devuelven ningún valor, por lo que se usa `.also { line = it }` para guardar la línea leída en la variable `line` al mismo tiempo que se comprueba si el resultado de `readLine()` fue `null` (fin del flujo).
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa BufferedReader para leer texto línea a línea desde la salida del proceso hijo
import java.io.BufferedReader
// Importa InputStreamReader para convertir los bytes que salen del hijo en caracteres
import java.io.InputStreamReader
// Importa OutputStreamWriter para convertir nuestros Strings de Kotlin en bytes que enviar a la entrada del hijo
import java.io.OutputStreamWriter
// Importa ProcessBuilder para configurar y lanzar el proceso del sistema operativo
import java.lang.ProcessBuilder

fun main() {
    // Comentario original de la profesora:
    // Crear un proceso para ejecutar el comando 'grep "kotlin"'
    // Como no se le indica ningún archivo a grep, este se queda escuchando por su entrada estándar (stdin)
    val processBuilder = ProcessBuilder("bash", "-c","grep kotlin")
    val process = processBuilder.start()

    // Comentario original de la profesora:
    // Escribir en el pipe de entrada del proceso
    // Conectamos un OutputStreamWriter al process.outputStream (que es el stdin del proceso grep)
    val writer = OutputStreamWriter(process.outputStream)
    // Enviamos la primera línea (contiene la palabra "kotlin", por lo que grep la dejará pasar)
    writer.write("kotlin es un lenguaje de programación\n")
    // Enviamos la segunda línea (no contiene "kotlin", por lo que grep la descartará)
    writer.write("java es otro lenguaje de programación\n")
    // Vacía el buffer forzando que los datos salgan inmediatamente hacia el proceso hijo
    writer.flush()
    // CRÍTICO: Cierra la tubería de entrada enviando EOF a grep para indicarle que ya hemos terminado de enviarle texto
    writer.close()

    // Comentario original de la profesora:
    // Leer la salida del proceso
    // Conectamos un BufferedReader al process.inputStream (que es el stdout del proceso grep)
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    var line: String?
    // Lee cada línea filtrada por grep, la guarda en 'line' mediante .also y sigue mientras no sea null (EOF)
    while (reader.readLine().also { line = it } != null) {
        // Solo imprimirá: "kotlin es un lenguaje de programación"
        println(line)
    }

    // Comentario original de la profesora:
    // Esperar a que el proceso termine
    // Bloquea al padre hasta que grep finalice y recoge su código de retorno (0 = encontró al menos una coincidencia)
    val exitCode = process.waitFor()
    println("Proceso terminado con código de salida: $exitCode")
}