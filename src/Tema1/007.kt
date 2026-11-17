/*
Este archivo implementa una tubería (pipe) programática entre dos procesos hijos independientes
utilizando el método InputStream.transferTo(OutputStream) para conectar la salida del primero
con la entrada del segundo desde Kotlin.
*/

/*
Puntos clave para estudio de 007.kt:

* Funcionamiento de transferTo():
    Lee todos los bytes que escupe el primer proceso por su salida estándar (echo.inputStream) y los escribe directamente en la entrada estándar del segundo proceso (tr.outputStream).
    Equivale a ejecutar en la terminal de Linux: echo hola | tr a-z A-Z

* Importancia vital de cerrar el outputStream del proceso receptor (tr.outputStream.close()):
    Comandos filtro como 'tr', 'grep', 'wc' o 'sort' se quedan leyendo de su entrada estándar indefinidamente hasta que reciben la señal de fin de archivo (EOF - End Of File).
    Si olvidamos poner tr.outputStream.close(), el comando 'tr' creerá que el padre aún puede enviarle más texto, nunca terminará, y nuestro programa se quedará colgado (deadlock) al intentar leer su salida en reader.lines().
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    // 1. Arranca el primer proceso hijo: ejecuta 'echo hola', que escribe "hola\n" en su stdout (echo.inputStream)
    val echo = ProcessBuilder("bash", "-c", "echo hola").start()

    // 2. Arranca el segundo proceso hijo: ejecuta 'tr a-z A-Z' (translate), que queda a la espera de recibir texto
    //    por su stdin (tr.outputStream) para convertir los caracteres de minúsculas (a-z) a mayúsculas (A-Z).
    val tr = ProcessBuilder("bash", "-c","tr a-z A-Z").start()

    // 3. Transfiere todos los bytes desde la salida estándar de 'echo' hacia la entrada estándar de 'tr'
    echo.inputStream.transferTo(tr.outputStream)

    // 4. IMPRESCINDIBLE: Cierra la entrada estándar de 'tr' para enviarle la señal EOF (Fin de Archivo)
    //    y que 'tr' procese los datos y termine su ejecución.
    tr.outputStream.close()

    // 5. Conectamos un BufferedReader a la salida estándar del segundo proceso ('tr.inputStream')
    val reader = BufferedReader(InputStreamReader(tr.inputStream))

    // 6. Leemos e imprimimos el resultado final transformado por 'tr': "HOLA"
    reader.lines().forEach { println(it) }
}