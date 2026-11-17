/*
Este archivo muestra la plantilla completa y robusta para lanzar un proceso con ProcessBuilder,
configurando su directorio de trabajo (.directory), unificando sus canales de salida y error
(.redirectErrorStream), capturando excepciones y esperando su código de retorno.
*/

/*
Puntos clave para estudio de Process.kt:

- Configuración del directorio de trabajo con .directory(File("ruta")):
    Si no se especifica, el proceso hijo se ejecuta en la carpeta raíz del proyecto de IntelliJ.
    Con .directory(File(...)), cambiamos la carpeta actual (working directory) exclusivamente para ese proceso hijo sin necesidad de usar el comando 'cd' dentro de bash.

- Por qué usar redirectErrorStream(true) con un único BufferedReader:
    Al ponerlo a 'true', cualquier mensaje de error que el comando escriba en stderr se redirige automáticamente a stdout (process.inputStream).
    Así nos ahorramos crear un segundo BufferedReader para process.errorStream y leemos tanto aciertos como fallos con un solo bucle while.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa BufferedReader para leer la salida del subproceso línea por línea
import java.io.BufferedReader
// Importa File para representar la ruta del directorio de trabajo en el sistema de archivos
import java.io.File
// Importa InputStreamReader para convertir los bytes del flujo de salida en caracteres
import java.io.InputStreamReader

fun main(){
    // Prepara el comando 'ls -l' (listado detallado de archivos) para ser ejecutado por bash (aún NO lo inicia)
    val processBuilder = ProcessBuilder("bash","-c","ls -l")

    // Comentario original de la profesora:
    // Establecer el directorio de trabajo
    // Indica que el comando 'ls -l' debe listar los archivos de esta ruta concreta y no la raíz del proyecto.
    // Nota: En cadenas de Kotlin, las barras invertidas de Windows deben escaparse como doble barra (\\).
    processBuilder.directory(File("C:\\Users\\Usuario\\Desktop\\2DAM-AS-2627\\Kotlin_Practicas\\src\\Tema1"))

    // Comentario original de la profesora:
    // Redirigir errores al flujo de salida
    // Fusiona stderr dentro de stdout para leer ambos canales desde process.inputStream
    processBuilder.redirectErrorStream(true)

    // Bloque try-catch para capturar posibles fallos al arrancar el proceso (ej. si la ruta del directorio no existe en nuestro PC, lanza IOException)
    try {

        // Comentario original de la profesora:
        // Iniciamos el proceso, start devuelve un objeto Process
        val process = processBuilder.start()

        // Comentario original de la profesora:
        // Leer la salida del proceso
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line:String?
        // Bucle clásico de lectura: lee una línea, la asigna a 'line' mediante .also y repite mientras no llegue al final del flujo (null)
        while(reader.readLine().also {line = it}!= null){
            println(line)
        }

        // Comentario original de la profesora:
        // Esperar a que el proceso finalice
        // Recoge el código de salida del proceso (0 si 'ls -l' funcionó correctamente)
        val exitCode = process.waitFor()
        println("Proceso terminado: $exitCode")
    }catch(e: Exception){
        // Si ocurre una excepción (por ejemplo, que la carpeta C:\Users\Usuario\... no exista en tu máquina), imprime la traza del error
        e.printStackTrace()
    }
}