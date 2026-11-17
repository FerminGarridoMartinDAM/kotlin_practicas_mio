/*
Este archivo enseña la forma clásica (anterior a Java 5) de crear procesos mediante el patrón
Singleton `Runtime.getRuntime().exec(...)`, explicando sus limitaciones frente a ProcessBuilder.
*/

/*
Puntos clave para estudio de Runtime.kt:

- Comparativa de examen: `Runtime.getRuntime().exec()` vs `ProcessBuilder`:
    1. Antigüedad: `Runtime.exec()` existe desde Java 1.0; `ProcessBuilder` se introdujo en Java 5 como su reemplazo moderno.
    2. Redirección y configuración: Con `ProcessBuilder` es facilísimo unir errores (`redirectErrorStream(true)`), heredar consola (`inheritIO()`) o redirigir a ficheros (`redirectOutput(...)`) antes de hacer `.start()`. Con `Runtime.exec()`, el proceso arranca directamente y no permite preconfigurar redirecciones.
    3. Obsolescencia de `exec(String)`: Pasar todo el comando en un solo String (`exec("ls -l")`) está **deprecated (obsoleto) desde Java 18** porque simplemente corta el texto por los espacios en blanco (usando `StringTokenizer`), rompiendo rutas con espacios como `"C:\\Program Files\\..."`. Siempre hay que pasar un array de Strings (`arrayOf(...)`).

- Detalle técnico importante sobre `arrayOf("bash", "-c", "ls", "-l")` en este archivo:
    Tal y como está escrito en el código original (`arrayOf("bash", "-c", "ls", "-l")`), como a `bash -c` se le pasan `"ls"` y `"-l"` en elementos separados del array, bash ejecuta `"ls"` (sin el `-l`) y guarda `"-l"` en la variable `$0`.
    - Si usas `bash -c`: lo ideal es `arrayOf("bash", "-c", "ls -l")`.
    - Si llamas directamente al binario en Linux sin bash: entonces sí se separa como `arrayOf("ls", "-l")`.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones para leer la salida estándar del proceso creado
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    try {
        // Runtime.getRuntime(): Devuelve la instancia única (Singleton) del entorno de ejecución de la JVM actual.
        // .exec(arrayOf(...)): Crea e inicia inmediatamente el proceso hijo (equivale al .start() de ProcessBuilder) y devuelve un objeto Process.
        val process = Runtime.getRuntime().exec(arrayOf("bash","-c","ls", "-l"))

        // Comentario original de la profesora (MUY IMPORTANTE PARA TEORÍA DE EXAMEN):
        // La versión Runtime.exec(String), que recibe el comando completo en una sola cadena,
        // está marcada como obsoleta (deprecated) desde Java 18,
        // porque divide el texto por espacios y falla con rutas o argumentos que los contienen.
        // Hay que usar la versión que recibe un array (exec(arrayOf("ls", "-l"))) o,
        // mejor aún, ProcessBuilder.

        // Conectamos un BufferedReader a la salida estándar (inputStream) del proceso hijo
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        // Leemos e imprimimos cada línea devuelta por el comando
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        // Esperamos a que el proceso termine y mostramos su código de salida (0 = éxito)
        val exitCode = process.waitFor()
        println("Proceso terminado con código de salida: $exitCode")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}