/*
Este archivo muestra cómo ejecutar el comando de monitorización de procesos 'ps' (Process Status)
en Linux/Bash y capturar su tabla de resultados desde Kotlin.
*/

/*
Puntos clave para estudio de 005.kt:

* Funcionamiento del comando 'ps':
    Ejecutado sin argumentos adicionales, 'ps' muestra una instantánea de los procesos activos asociados a la terminal/sesión actual, devolviendo columnas como PID (identificador del proceso), TTY (terminal), TIME (tiempo de CPU) y CMD (comando ejecutado).

* Lectura del flujo hasta el cierre del proceso:
    El método reader.lines().forEach { ... } consume las líneas a medida que 'ps' las escribe en su stdout y termina automáticamente cuando el comando 'ps' finaliza y cierra su flujo de salida.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones para envolver el flujo de bytes del proceso y leerlo como texto línea a línea
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    // Lanza el intérprete bash para que ejecute el comando 'ps'
    val process = ProcessBuilder("bash","-c","ps").start()

    // Abre el canal de lectura con buffer sobre la salida estándar del comando 'ps'
    val reader = BufferedReader(InputStreamReader(process.inputStream))

    // Recorre todas las líneas devueltas (la cabecera de la tabla y cada proceso activo) y las muestra por consola
    reader.lines().forEach { println(it) }
}