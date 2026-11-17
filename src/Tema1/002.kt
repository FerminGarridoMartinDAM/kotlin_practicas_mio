/*
Este archivo profundiza en cómo pasar modificadores (flags), argumentos adicionales
y tuberías (pipes) a los comandos ejecutados dentro de cmd, powershell y bash.
*/

/*
Puntos clave para estudio de 002.kt:

* Cómo agrupar argumentos en ProcessBuilder según el intérprete:
    - En CMD: Puedes separar cada elemento como Strings individuales ("cmd", "/c", "dir", "/q") porque cmd.exe concatena todo lo que recibe tras /c.
    - En Bash (-c) y PowerShell (-Command): Es mucho más seguro pasar toda la orden compuesta dentro de un único String tras el flag (por ejemplo, "ls -l" o "Get-ChildItem | Format-List") para que el intérprete procese los espacios, argumentos y tuberías correctamente.

* Uso de tuberías internas del intérprete:
    Al ejecutar "Get-ChildItem | Format-List" dentro de PowerShell, la tubería (|) la resuelve el propio intérprete PowerShell antes de devolver el resultado final a Kotlin por el inputStream.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones necesarias para leer el flujo de salida del proceso línea a línea
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    // Ejecuta el intérprete "cmd" de Windows pasando cada token como un argumento separado en ProcessBuilder:
    // - "cmd": El ejecutable del intérprete de comandos de Windows.
    // - "/c": Bandera correcta que ordena a CMD ejecutar el comando y finalizar la consola.
    // - "dir": Comando interno para listar archivos y carpetas del directorio de trabajo actual.
    // - "/q": Modificador del comando 'dir' que muestra el propietario (owner) de cada archivo.
    val process = ProcessBuilder("cmd", "/c", "dir", "/q").start()

    // Creamos el lector con buffer conectado a la salida estándar (stdout) del subproceso
    val reader = BufferedReader(InputStreamReader(process.inputStream))

    // Consumimos e imprimimos cada línea generada por el comando 'dir /q'
    reader.lines().forEach { println(it) }
}


/*
// Variante comentada 1: Uso de tuberías (pipes) internas en PowerShell
import java.io.BufferedReader
import java.io.InputStreamReader

fun Poo.main() {
    // Todo lo que va en el String después de "-Command" es la orden que interpreta PowerShell.
    // "Get-ChildItem | Format-List": Obtiene la lista de elementos del directorio actual y la redirige (|)
    // al cmdlet Format-List, que muestra todas las propiedades de cada fichero en formato de lista vertical detallada.
    val process = ProcessBuilder("powershell", "-Command", "Get-ChildItem | Format-List").start()

    // Cerramos el flujo de entrada estándar del hijo (stdin) para evitar que PowerShell quede bloqueado esperando entrada
    process.outputStream.close()

    // Leemos y mostramos el resultado línea por línea
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.lines().forEach { println(it) }
}
*/

/*
// Variante comentada 2: Listado detallado en Bash
import java.io.BufferedReader
import java.io.InputStreamReader

fun Poo.main() {
    // Invoca a bash con "-c" y le pasa "ls -l" como un único String.
    // El flag "-l" (long format) muestra permisos rwx, número de enlaces, propietario, grupo, tamaño en bytes, fecha y nombre.
    val process = ProcessBuilder("bash","-c","ls -l").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.lines().forEach { println(it) }
}
*/