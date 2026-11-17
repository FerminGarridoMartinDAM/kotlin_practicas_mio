/*
Este archivo actúa como proceso padre ("Lanzador"): levanta una nueva instancia de la
Máquina Virtual de Java (JVM) para ejecutar el archivo empaquetado hijo.jar (creado a partir de 009hijo.kt),
captura toda su salida por consola y muestra su código de finalización.
*/

/*
Puntos clave para estudio de 009Lanzador.kt:

* Ejecución de un .jar como proceso hijo:
    No podemos pasar directamente "hijo.jar" como primer argumento de ProcessBuilder porque un archivo .jar no es un binario nativo del sistema operativo (.exe o ELF).
    Debemos invocar al ejecutable "java" con la opción "-jar" seguida de la ruta al fichero ("src/Tema1/hijo.jar"). Esto crea un nuevo proceso del sistema operativo con su propia JVM independiente.

* Encadenamiento de métodos (Method Chaining) en ProcessBuilder:
    ProcessBuilder permite configurar opciones como .redirectErrorStream(true) y llamar directamente a .start() en una sola expresión fluida.
    Con .redirectErrorStream(true), si el proceso hijo lanzara alguna excepción (por ejemplo, FileNotFoundException si la ruta al .jar estuviera mal), el mensaje de error de Java saldría por el mismo canal process.inputStream y lo veríamos en consola prefijado con "[hijo]: ".
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

fun main() {
    // Comentario original del profesor:
    // El proceso hijo es una nueva JVM que ejecuta hijo.jar
    // 1. ProcessBuilder("java", "-jar", "src/Tema1/hijo.jar"): Prepara el comando para lanzar java ejecutando el jar.
    // 2. .redirectErrorStream(true): Fusiona la salida de error (stderr) con la salida estándar (stdout) del hijo.
    // 3. .start(): Inicia el subproceso y devuelve el objeto Process.
    val process = ProcessBuilder("java", "-jar", "src/Tema1/hijo.jar")
        .redirectErrorStream(true)
        .start()

    // Obtiene un BufferedReader sobre la salida del hijo (process.inputStream),
    // recorre todas las líneas que el hijo imprima y las muestra en la consola del padre añadiendo la etiqueta "[hijo]: " delante.
    process.inputStream.bufferedReader().lines().forEach { println("[hijo]: $it") }

    // process.waitFor() bloquea al padre hasta que la JVM hija termine y devuelve su código de salida (0 si todo fue bien).
    println("El hijo terminó con código ${process.waitFor()}")
}