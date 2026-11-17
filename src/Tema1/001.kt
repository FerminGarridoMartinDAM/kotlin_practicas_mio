/*
Este archivo introduce la creación de subprocesos mediante ProcessBuilder
invocando a los tres intérpretes de comandos principales según el entorno
(bash en Linux/WSL, powershell en Windows y cmd en Windows)
y la lectura de su flujo de salida estándar.
*/

/*
Puntos clave para estudio de 001.kt:

* Comandos internos vs. ejecutables externos:
    Comandos como cd, dir o Get-ChildItem no son programas independientes en disco, sino funciones internas de la shell.
    Si haces ProcessBuilder("dir").start() directamente, fallará con una excepción (IOException) porque el sistema operativo busca un archivo dir.exe que no existe.

* Nomenclatura invertida de los Streams en la clase Process:
    process.inputStream  -> Conectado al stdout (salida normal) del hijo.
    process.outputStream -> Conectado al stdin (entrada de datos) del hijo.
    process.errorStream  -> Conectado al stderr (salida de errores) del hijo.
*/

// Define que este archivo pertenece al paquete Tema1 dentro de la estructura del proyecto
package Tema1

// Importa BufferedReader: envuelve un Reader para leer texto de forma eficiente usando un buffer en memoria (permite leer líneas enteras)
import java.io.BufferedReader
// Importa InputStreamReader: actúa como puente traduciendo flujos de bytes (InputStream) a flujos de caracteres (Reader) según la codificación
import java.io.InputStreamReader


fun main() {
    // 1. ProcessBuilder("bash", "-c", "cd / && ls"):
    //    - "bash": Invoca al intérprete de comandos Bourne Again SHell.
    //    - "-c": Flag que indica a bash que lea y ejecute el comando contenido en el siguiente String.
    //    - "cd / && ls": Comando compuesto; primero cambia al directorio raíz (/) y, solo si tuvo éxito (operador lógico &&), ejecuta 'ls' para listar su contenido.
    // 2. .start(): Solicita al Sistema Operativo que cree un nuevo proceso hijo y comience su ejecución inmediatamente. Devuelve un objeto de tipo Process.
    val process = ProcessBuilder("bash","-c","cd / && ls").start()

    // process.inputStream: Aunque se llame "inputStream", representa la SALIDA estándar (stdout) del proceso hijo.
    // Se llama inputStream porque, desde el punto de vista de nuestro programa padre en Kotlin, los datos "entran" hacia nosotros.
    // Se envuelve en InputStreamReader (bytes -> caracteres) y luego en BufferedReader (caracteres -> líneas).
    val reader = BufferedReader(InputStreamReader(process.inputStream))

    // reader.lines(): Devuelve un Stream<String> de Java 8+ con todas las líneas que el proceso hijo va imprimiendo.
    // .forEach { println(it) }: Recorre el flujo e imprime por la consola de Kotlin cada línea ('it') hasta que el proceso hijo cierra su salida.
    reader.lines().forEach { println(it) }
}

/*
// Segundo ejemplo (comentado): Ejecución de un cmdlet en Windows PowerShell
fun Poo.main() {
    // Get-ChildItem es un cmdlet interno de PowerShell (equivalente a ls o dir).
    // No es un archivo .exe independiente, por lo que es obligatorio llamar primero al ejecutable "powershell" con el parámetro "-Command".
    val process = ProcessBuilder("powershell", "-Command", "Get-ChildItem").start()

    // DETALLE CRÍTICO EN POWERSHELL:
    // process.outputStream representa la ENTRADA estándar (stdin) del proceso hijo.
    // PowerShell a menudo se queda esperando a que su entrada estándar se cierre antes de terminar de ejecutarse.
    // Al hacer .close(), le enviamos un EOF (End Of File) indicándole que no le vamos a enviar más comandos ni datos por teclado.
    process.outputStream.close() // Cerramos la entrada para que PowerShell no espere datos

    // Leemos la salida estándar del proceso de PowerShell igual que en el caso anterior
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.lines().forEach { println(it) }
}
*/

/*
// Tercer ejemplo (comentado): Ejecución de un comando interno en el símbolo del sistema (CMD de Windows)
fun Poo.main() {
    // 'dir' no existe como archivo dir.exe en C:\Windows\System32; está integrado dentro de cmd.exe.
    // Por eso debemos invocar a "cmd".
    // OJO / ERRATA EN EL CÓDIGO ORIGINAL: El modificador está escrito como "c/", pero en Windows CMD la sintaxis correcta es "/c"
    // ("/c" ejecuta el comando que le sigue y cierra la instancia de cmd; "/k" la mantendría abierta).
    val process = ProcessBuilder("cmd", "c/", "dir").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.lines().forEach { println(it) }
}
*/