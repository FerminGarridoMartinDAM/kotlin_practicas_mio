/*
Este archivo proporciona una plantilla completa y robusta para ejecutar scripts externos (.sh, .bat, .py),
configurando su directorio de trabajo, unificando la salida de errores, leyendo la salida con el bucle
clásico de lectura en Kotlin y gestionando posibles excepciones con try-catch.
*/

/*
Puntos clave para estudio de EjecucionScripts.kt:

* Configuración previa al .start() en ProcessBuilder:
    1. Paso de parámetros al script: Se pasan como argumentos separados en el constructor: ProcessBuilder("ruta/script.sh", "arg1", "arg2"). Dentro del script bash se recibirán como $1 y $2.
    2. .directory(File("...")): Cambia el directorio de trabajo (working directory) en el que se ejecutará el proceso hijo. Si no se especifica, el hijo hereda el directorio de trabajo del proceso padre (normalmente la raíz del proyecto en IntelliJ).
    3. .redirectErrorStream(true): Hace que tanto la salida normal (stdout) como los errores (stderr) del script viajen juntos por process.inputStream.

* Explicación de la expresión idiomática while (reader.readLine().also { line = it } != null):
    En Java se suele escribir: while ((line = reader.readLine()) != null).
    En Kotlin, la asignación '=' no devuelve un valor (no es una expresión), por lo que no puede meterse directamente dentro de la condición del while.
    Por eso se usa .also { line = it }:
    1. reader.readLine() lee una línea del proceso (o devuelve null si el proceso ya terminó y cerró el flujo).
    2. .also { line = it } guarda ese valor leído ('it') en la variable 'line' y devuelve el mismo valor original.
    3. Se compara con '!= null': si había línea, entra al bucle y la imprime; si era null (fin del flujo), sale del bucle.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones para manejo de directorios (File), lectura de flujos (InputStreamReader, BufferedReader) y creación de procesos
import java.io.File
import java.io.InputStreamReader
import java.io.BufferedReader
import java.lang.ProcessBuilder


fun main() {
    // Crear un proceso para ejecutar un script externo indicando su ruta
    val processBuilder = ProcessBuilder("path/to/your/script.sh")
    /*
    Comentario original del profesor:
    Si queremos pasar algo como parámetro al script:
        val processBuilder = ProcessBuilder("path/to/your/script.sh", "arg1", "arg2")
     */
    // Establece la carpeta desde la cual se ejecutará el script (afecta a las rutas relativas que use el script por dentro)
    processBuilder.directory(File("/path/to/your/script/directory")) // Establecer el directorio de trabajo

    // Une el canal de errores (stderr) con el canal de salida estándar (stdout)
    processBuilder.redirectErrorStream(true) // Redirigir errores al flujo de salida

    // Bloque try-catch para capturar errores (por ejemplo, IOException si el script no existe o no tiene permisos de ejecución +x)
    try {
        // Arranca el script en el sistema operativo
        val process = processBuilder.start() // Iniciar el proceso

        // Crea el lector con buffer conectado a la salida del proceso hijo
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        // Variable nullable donde iremos guardando cada línea leída
        var line: String?

        // Lee línea a línea del proceso, la asigna a 'line' mediante .also y repite mientras no llegue al final del flujo (null)
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        // Bloquea hasta que el script finalice por completo y guarda su código de retorno (0 = éxito)
        val exitCode = process.waitFor()
        println("Proceso terminado con código de salida: $exitCode")
    } catch (e: Exception) {
        // Imprime la traza del error por consola si falló el arranque o la ejecución del proceso
        e.printStackTrace()
    }
}