/*
Este archivo demuestra la ejecución de un subproceso en primer plano (foreground)
utilizando inheritIO() para conectar directamente los flujos de entrada, salida y error
del hijo con la consola del proceso padre en tiempo real.
*/

/*
Puntos clave para estudio de PrimerPlano.kt:

- ¿Qué hace exactamente .inheritIO()?
    Por defecto, ProcessBuilder redirige stdin, stdout y stderr del hijo a tuberías internas (pipes) que debemos leer manualmente con BufferedReader.
    Al llamar a .inheritIO(), el proceso hijo hereda la misma consola que nuestra aplicación Kotlin.
    Ventaja: No hace falta crear ningún BufferedReader y la salida (como cada respuesta de ping segundo a segundo) se ve en pantalla en tiempo real.
    Desventaja: Nuestro programa Kotlin no puede guardar ni procesar en variables el texto que imprime el hijo, porque va directo a la pantalla.

- ¿Qué significa ejecutar en "Primer Plano" (Foreground)?
    Significa que el proceso padre lanza al hijo y se queda bloqueado en process.waitFor() esperando a que el hijo termine antes de continuar con sus propias instrucciones.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

fun main() {
    // Configura e inicia el comando 'ping -c 3 localhost' dentro de bash:
    // - "ping": Herramienta de red que envía paquetes ICMP Echo Request para comprobar la conectividad.
    // - "-c 3": En Linux/bash indica "count 3" (envía exactamente 3 paquetes y termina; en Windows sería "-n 3", porque sin límite en Linux el ping es infinito).
    // - "localhost": Apunta a nuestra propia máquina (127.0.0.1).
    val process = ProcessBuilder("bash","-c", "ping -c 3 localhost")
        // Comentario original de la profesora:
        .inheritIO() // El hijo escribe directamente en la consola del padre
        // Arranca el proceso hijo en el sistema operativo
        .start()

    // Comentario original de la profesora (solución a un error frecuente en WSL):
    // Si da fallo en Windows en Socket: Entrar en WSL y poner:
    // getcap $(which ping)
    // sudo setcap cap_net_raw+ep /usr/bin/ping
    // (Explicación: 'ping' necesita permisos especiales de red 'cap_net_raw' para abrir sockets crudos ICMP sin ser usuario root).

    // process.pid(): Devuelve un Long con el identificador único del proceso (Process ID) asignado por el Sistema Operativo
    println("Proceso iniciado con PID: ${process.pid()}")

    // Comentario original de la profesora:
    // El padre se bloquea hasta que el hijo termine
    // Como tarda unos 3 segundos en hacer los 3 pings, el hilo main se detiene aquí hasta que ping finaliza y devuelve su código (0 = éxito)
    val exitCode = process.waitFor()
    println("Proceso terminado con código de salida: $exitCode")
}