/*
Este archivo desarrolla un ejercicio práctico completo en 6 pasos que combina la gestión de ficheros
temporales desde Kotlin (File.createTempFile) con la ejecución secuencial de tres procesos en Bash
que utilizan redirecciones de salida (>) y de entrada (<) para escribir, contar líneas y leer el archivo.
*/

/*
Puntos clave para estudio de 011.kt:

* Creación y borrado de archivos temporales (File.createTempFile y deleteOnExit):
    - File.createTempFile("practica", ".txt") crea un fichero real en el directorio temporal del sistema operativo (p. ej., /tmp en Linux o C:\Users\...\AppData\Local\Temp en Windows), añadiendo un número aleatorio en medio del nombre (ej. practica84920192.txt) para evitar colisiones.
    - archivoTemporal.deleteOnExit() registra un gancho (shutdown hook) en la JVM para que el archivo se elimine automáticamente cuando el programa Kotlin termine de ejecutarse.

* Redirecciones en Bash (> y <):
    - Operador '>': Redirige la salida estándar (stdout) de un comando hacia un archivo, sobrescribiendo su contenido (usado con printf). (Si fuera '>>', añadiría al final sin borrar).
    - Operador '<': Redirige el contenido de un archivo hacia la entrada estándar (stdin) de un comando (usado en "wc -l < archivo", lo que hace que wc -l imprima solo el número "4" en lugar de "4 ruta/al/archivo").

* Por qué es obligatorio usar .waitFor() entre cada paso:
    Los 3 procesos (escribir, contar y leer) operan sobre el mismo fichero físico.
    Si no llamáramos a escribir.waitFor() antes de lanzar el proceso 'contar', se produciría una condición de carrera (race condition): 'wc -l' y 'cat' podrían leer el archivo cuando 'printf' aún no ha terminado de escribir las 4 líneas en el disco, devolviendo 0 líneas o texto incompleto.

* Diferencia entre .lines().forEach y .readText():
    Aquí se usa .bufferedReader().readText(), que lee todo el flujo de salida del proceso de golpe y lo devuelve en un único String (incluyendo saltos de línea), aplicando .trim() en el contador para limpiar espacios o saltos de línea alrededor del número.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa la clase File de java.io para crear y gestionar el archivo temporal en el sistema de ficheros
import java.io.File

fun main() {

    // ==========================================================
    // PASO 1: Crear un archivo temporal
    // ==========================================================

    // Crea un fichero vacío en la carpeta temporal del sistema con prefijo "practica" y extensión ".txt"
    val archivoTemporal = File.createTempFile("practica",".txt")
    println("Archivo temporal creado:")
    // .absolutePath obtiene la ruta completa en texto (ej. /tmp/practica12345.txt) para pasársela luego a bash
    println(archivoTemporal.absolutePath)
    println()

    // ==========================================================
    // PASO 2: Escribir contenido usando printf desde bash
    // ==========================================================
    // printf enviará varias líneas al archivo temporal.
    //
    // > redirige la salida al archivo.
    //
    // Lanzamos bash ejecutando printf con 4 saltos de línea (\n) y redirigiendo (>) el resultado al fichero temporal
    val escribir = ProcessBuilder(
        "bash",
        "-c",
        "printf 'Línea 1\nLínea 2\nLínea 3\nLínea 4\n' > ${archivoTemporal.absolutePath}"
    ).start()

    // Bloqueamos el programa padre hasta que el proceso 'escribir' haya volcado los datos y cerrado el archivo
    escribir.waitFor()

    // ==========================================================
    // PASO 3: Contar líneas usando wc -l
    // ==========================================================
    // Lanzamos el comando 'wc -l' (word count, opción -l para contar líneas) inyectándole el archivo por stdin con '<'
    val contar = ProcessBuilder(
        "bash",
        "-c",
        "wc -l < ${archivoTemporal.absolutePath}"
    ).start()

    // Leemos toda la salida de 'wc -l' como un único String con readText() y eliminamos espacios/saltos de línea con trim()
    val numeroLineas = contar.inputStream
        .bufferedReader()
        .readText()
        .trim()

    // Esperamos a que el proceso 'contar' finalice completamente
    contar.waitFor()

    // Mostramos el total de líneas contadas (debe mostrar 4)
    println("Número de líneas: $numeroLineas")
    println()

    // ==========================================================
    // PASO 4: Leer contenido usando cat
    // ==========================================================
    // Lanzamos el comando 'cat' pasándole la ruta del archivo temporal para que vuelque su contenido por stdout
    val leer = ProcessBuilder(
        "bash",
        "-c",
        "cat ${archivoTemporal.absolutePath}"
    ).start()

    // Capturamos todo el texto devuelto por 'cat' en la variable 'contenido'
    val contenido = leer.inputStream
        .bufferedReader()
        .readText()

    // Esperamos a que el proceso 'leer' termine
    leer.waitFor()

    // ==========================================================
    // PASO 5: Mostrar contenido
    // ==========================================================
    println("Contenido del archivo:")
    println("----------------------")
    println(contenido)

    // ==========================================================
    // PASO 6: Eliminar archivo temporal
    // ==========================================================
    // Programa el borrado automático del fichero temporal justo cuando la máquina virtual (JVM) finalice
    archivoTemporal.deleteOnExit()
}