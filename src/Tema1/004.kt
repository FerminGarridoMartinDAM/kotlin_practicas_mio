/*
Este archivo introduce la sincronización básica entre el proceso padre y el proceso hijo
mediante el método bloqueante waitFor().
*/

/*
Puntos clave para estudio de 004.kt:

* Comportamiento de waitFor():
    Convierte la ejecución del subproceso en síncrona respecto al hilo que lo llama.
    El hilo principal del padre se queda pausado (bloqueado) en la línea process.waitFor() hasta que el proceso hijo termina en el sistema operativo.
    Además, waitFor() devuelve un Int con el código de salida (exit code: 0 = éxito, distinto de 0 = error).
    Si no pusiéramos waitFor(), "Proceso finalizado." se imprimiría inmediatamente después de arrancar el hijo, sin esperar a que termine.

* Trampa técnica con "bash", "-c" y argumentos separados:
    En este código se escribió ProcessBuilder("bash", "-c", "sleep", "2").
    En Bash, la opción "-c" solo lee el PRIMER String siguiente ("sleep") como comando a ejecutar, y asigna los siguientes Strings ("2") a las variables posicionales ($0, $1...).
    Por tanto, bash ejecuta "sleep" sin argumentos, falla inmediatamente por "falta un operando" y no duerme los 2 segundos.
    Para que funcione correctamente con bash -c debe escribirse en un solo String: ProcessBuilder("bash", "-c", "sleep 2")
    o bien invocar directamente al binario sin bash: ProcessBuilder("sleep", "2").
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

fun main() {
    // Mensaje impreso por el proceso padre antes de lanzar el proceso hijo
    println("Iniciando proceso...")

    // Crea y arranca el proceso hijo.
    // (Nota: Recuerda que en bash -c conviene unir "sleep 2" en un solo String para que no ignore el "2").
    val process = ProcessBuilder("bash","-c","sleep", "2").start()

    // process.waitFor(): Bloquea la ejecución del proceso padre en esta línea
    // hasta que el proceso hijo haya terminado por completo en el sistema operativo.
    process.waitFor()

    // Esta línea solo se alcanza y ejecuta una vez que el subproceso ha finalizado
    println("Proceso finalizado.")
}