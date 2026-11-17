/*
Este archivo realiza un muestreo periódico (polling) de los procesos que más CPU consumen
en el sistema operativo utilizando un bucle repeat, pausas con Thread.sleep, tuberías nativas de Bash
y la sintaxis funcional idiomática de Kotlin para lectura de flujos.
*/

/*
Puntos clave para estudio de 008.kt:

* Desglose del comando de Bash "ps -eo pid,comm,%cpu --sort=-%cpu | head -n 4":
    - ps -e: Selecciona todos los procesos activos del sistema.
    - -o pid,comm,%cpu: Indica el formato de salida mostrando solo 3 columnas (PID, nombre del comando y % de uso de CPU).
    - --sort=-%cpu: Ordena los resultados por la columna %cpu en orden descendente (el signo '-' indica de mayor a menor).
    - | head -n 4: Envía la tabla por una tubería a 'head' para cortar y mostrar únicamente las 4 primeras líneas (la línea de títulos + el Top 3 de procesos).

* Lectura idiomática en Kotlin:
    En lugar de escribir BufferedReader(InputStreamReader(top.inputStream)), Kotlin ofrece la función de extensión .bufferedReader() directamente sobre cualquier InputStream.
    Además, .forEach(::println) utiliza una referencia a función (::println), que es más concisa que escribir { println(it) }.
*/

// Nota: En el archivo original se omitió 'package Tema1', por lo que si está en la carpeta Tema1
// el IDE puede sugerir añadir 'package Tema1' al principio del fichero.
fun main() {

    // repeat(3) { ... }: Ejecuta el bloque de código 3 veces seguidas.
    // En cada vuelta, la variable implícita 'it' vale 0, 1 y 2 respectivamente.
    repeat(3) {

        // Pausa el hilo actual durante 2000 milisegundos (2 segundos) entre cada muestra
        Thread.sleep(2000)

        // Lanza el subproceso en bash que obtiene el Top 3 de procesos con mayor consumo de CPU
        val top = ProcessBuilder("bash",
            "-c",
            "ps -eo pid,comm,%cpu --sort=-%cpu | head -n 4"
        ).start()

        // Imprime el encabezado de la muestra actual (sumando 1 a 'it' para que muestre 1, 2 y 3)
        println("=== Muestra ${it + 1} ===")

        // .bufferedReader(): Extensión de Kotlin que envuelve el InputStream en un BufferedReader (UTF-8 por defecto).
        // .lines(): Obtiene el Stream de líneas de texto.
        // .forEach(::println): Imprime cada línea por consola usando referencia a método.
        top.inputStream.bufferedReader()
            .lines()
            .forEach(::println)
    }
}