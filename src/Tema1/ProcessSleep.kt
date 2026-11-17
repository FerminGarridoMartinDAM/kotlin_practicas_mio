/*
Este archivo muestra cómo lanzar un proceso de larga duración ('sleep 10') con ProcessBuilder
y cómo el proceso padre queda sincronizado esperando a que finalice tanto al leer su flujo
de salida como al invocar waitFor().
*/

/*
Puntos clave para estudio de ProcessSleep.kt:

- Diferencia importante respecto a 004.kt (Cómo interpreta bash el flag -c):
    - En 004.kt estaba escrito: ProcessBuilder("bash", "-c", "sleep", "2"). Cuando usas "-c", bash solo lee el PRIMER String que va tras "-c" como comando ("sleep") y asigna los siguientes Strings ("2") como parámetros posicionales ($0, $1...), por lo que 'sleep' fallaba por falta de argumentos.
    - Aquí en ProcessSleep.kt está escrito correctamente: ProcessBuilder("bash", "-c", "sleep 10 "). Al ir "sleep 10 " dentro del mismo String, bash sí le pasa el 10 al comando sleep y el proceso dura exactamente 10 segundos.

- ¿Dónde espera realmente los 10 segundos nuestro programa padre en este código?
    Aunque parezca que espera en `process.waitFor()`, en realidad se queda esperando 10 segundos en la línea `while (reader.readLine().also { line = it } != null)`.
    ¿Por qué? Porque `readLine()` es una operación bloqueante: como `sleep 10` no imprime nada pero mantiene su tubería stdout abierta mientras está vivo, `readLine()` se queda esperando 10 segundos hasta que `sleep` termina y el sistema operativo cierra la tubería (devolviendo `null`). Cuando llega a `waitFor()`, el proceso acaba de terminar y devuelve `0` al instante.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones para el manejo de flujos de lectura y creación de procesos
import java.io.File
import java.io.InputStreamReader
import java.io.BufferedReader
import java.lang.ProcessBuilder
import java.util.concurrent.TimeUnit

fun main() {
    // Comentario original de la profesora:
    // Crear un proceso para ejecutar el comando 'sleep'
    // Sleep sirve para crear un proceso que se ejecute en segundo plano
    // (Nota de estudio: 'sleep 10' pausa al subproceso durante 10 segundos simulando una tarea pesada o en segundo plano,
    // aunque en este código el padre esperará a que termine antes de finalizar).
    val processBuilder = ProcessBuilder("bash","-c","sleep 10 ")

    // Comentario original de la profesora:
    // Fusiona el canal de errores (stderr) con el canal de salida estándar (stdout)
    processBuilder.redirectErrorStream(true) // Redirigir errores al flujo de salida

    try {
        // Comentario original de la profesora:
        val process = processBuilder.start() // Iniciar el proceso

        // Comentario original de la profesora:
        // Leer la salida del proceso
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        // 'sleep 10' no escribe texto por consola, pero este bucle se queda bloqueado durante los 10 segundos
        // esperando a que el proceso hijo cierre su flujo de salida al terminar
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        // Comentario original de la profesora:
        // Esperar a que el proceso termine
        // Asegura que el proceso hijo haya finalizado en el SO y recoge su código de salida (0 = terminó sin errores)
        val exitCode = process.waitFor()
        println("Proceso terminado con código de salida: $exitCode")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}