/*
Este archivo define un programa sencillo en Kotlin diseñado para ser compilado como un paquete
ejecutable independiente (hijo.jar) que posteriormente será invocado como proceso hijo
desde otro programa Kotlin (009Lanzador.kt).
*/

/*
Puntos clave para estudio de 009hijo.kt:

* Compilación desde línea de comandos con kotlinc:
    Para que un archivo .kt pueda ejecutarse mediante "java -jar", debemos compilarlo usando el compilador de Kotlin (kotlinc) con dos parámetros clave:
    1. -include-runtime: Empaqueta dentro del archivo .jar la librería estándar de Kotlin (kotlin-stdlib). Si no se incluye, al intentar ejecutarlo con "java -jar hijo.jar" fallará con un error NoClassDefFoundError porque la JVM estándar no trae las librerías de Kotlin preinstaladas.
    2. -d hijo.jar: Indica el destino (destination) o nombre del archivo .jar ejecutable resultante, generando automáticamente el archivo MANIFEST.MF con la clase principal (Main-Class: Tema1._009hijoKt).

* Comunicación con el proceso padre:
    Cualquier llamada a print() o println() dentro de este programa escribe en su salida estándar (stdout). Cuando 009Lanzador.kt lo ejecute como subproceso, esa salida quedará conectada al canal process.inputStream del padre.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1


// Comentario original del profesor con la ruta al compilador kotlinc integrado en IntelliJ IDEA:
// El operador '&' en PowerShell (call operator) permite ejecutar un comando cuya ruta está entre comillas y contiene espacios.
// & "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.2\plugins\Kotlin\kotlinc.ide\bin\kotlinc.bat"
// 009hijo.kt -include-runtime -d hijo.jar

// Comentario original del profesor:
// Lo podemos añadir al PATH si vemos que lo vamos a usar mucho

fun main() {
    // Imprime un saludo por la salida estándar (stdout) del proceso hijo y termina su ejecución con código 0
    println("¡Hola desde el proceso hijo!")
}