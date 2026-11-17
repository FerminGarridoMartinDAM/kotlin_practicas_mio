/*
Este archivo introduce la interfaz ProcessHandle (añadida en Java 9+), que permite inspeccionar,
monitorizar y gestionar tanto subprocesos creados por nosotros como cualquier otro proceso
activo en el Sistema Operativo, incluido nuestro propio programa en ejecución.
*/

/*
Puntos clave para estudio de ProcessHandle.kt:

- Diferencia entre `Process` y `ProcessHandle`:
    - `Process`: Representa únicamente a un proceso hijo que hemos lanzado nosotros mismos con `ProcessBuilder.start()` o `Runtime.exec()`, dándonos acceso a sus tuberías (`inputStream`, `outputStream`, `errorStream`).
    - `ProcessHandle`: Es un manejador ligero que sirve para consultar información del sistema operativo (PID, usuario propietario, comando ejecutado, hora de inicio, procesos padre/hijos) o matar procesos (`destroy()`), tanto si los hemos creado nosotros (`process.toHandle()`) como si son procesos externos del sistema (`ProcessHandle.allProcesses()`).

- Uso de `Optional` en `processHandle.info()`:
    Como según los permisos del sistema operativo algunos datos pueden estar ocultos o no disponibles, los métodos de `ProcessHandle.Info` (`command()`, `user()`, `startInstant()`) devuelven un objeto `Optional`. Por eso se usa `.orElse("desconocido")` para dar un valor por defecto si el dato no está disponible.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa ProcessBuilder
import java.lang.ProcessBuilder

fun main() {
    // Comentario original de la profesora:
    // Crear un proceso para ejecutar el comando 'sleep 10'
    // El comando 'sleep 10' mantiene vivo al proceso hijo durante 10 segundos sin hacer nada
    val processBuilder = ProcessBuilder("bash", "-c", "sleep 10 ")
    try {
        // Comentario original de la profesora:
        val process = processBuilder.start() // Iniciar el proceso
        // Convierte nuestro objeto Process en un ProcessHandle para acceder a la API de monitorización
        val processHandle = process.toHandle() // Obtener el ProcessHandle del proceso

        // Obtiene e imprime el identificador numérico (PID) del proceso hijo en el sistema operativo
        println("Proceso iniciado con PID: ${processHandle.pid()}")

        /*
        // Bloque comentado por la profesora: Consulta de metadatos del proceso mediante ProcessHandle.Info
        // isAlive(): Devuelve true si el proceso hijo todavía se está ejecutando en el SO
        println("¿El proceso está vivo? ${processHandle.isAlive()}")
        // info(): Toma una instantánea (snapshot) con los metadatos del proceso en el sistema operativo
        val info = processHandle.info()
        // command(): Ruta del ejecutable (ej. /usr/bin/sleep o /bin/bash). Si el SO no lo proporciona, devuelve "desconocido"
        println("Comando: ${info.command().orElse("desconocido")}")
        // user(): Nombre del usuario del sistema operativo propietario del proceso
        println("Usuario: ${info.user().orElse("desconocido")}")
        // startInstant(): Momento exacto (fecha y hora UTC) en el que arrancó el proceso
        println("Inicio: ${info.startInstant().orElse(null)}")
         */

        // Comentario original de la profesora:
        // Esperar a que el proceso termine
        // El padre espera los 10 segundos que dura el sleep y recoge el código de salida (0)
        val exitCode = process.waitFor()
        println("Proceso terminado con código de salida: $exitCode")
    } catch (e: Exception) {
        e.printStackTrace()
    }

// Comentario original de la profesora:
// ProcessHandle también permite consultar procesos que no hemos lanzado nosotros.
    /*
     // Ejemplo comentado 1: Obtener el PID de nuestra propia máquina virtual Java (el proceso padre actual)
     val actual = ProcessHandle.current()
     println("PID de esta JVM: ${actual.pid()}")
    */

// Comentario original de la profesora:
// Por ejemplo, podemos obtener el proceso actual o recorrer todos los procesos del sistema:
    /*
     // Ejemplo comentado 2: Listar procesos del sistema operativo sin usar comandos externos como 'ps' o 'tasklist'
     // ProcessHandle.allProcesses(): Devuelve un Stream con todos los procesos visibles en el SO para el usuario actual
     ProcessHandle.allProcesses()
        // Filtra solo aquellos procesos de los que tengamos permiso para ver su ruta de comando
        .filter { it.info().command().isPresent }
        // Se queda solo con los primeros 10 procesos para no saturar la consola
        .limit(10)
        // Imprime el PID y el comando ejecutable de cada uno de esos 10 procesos
        .forEach { println("${it.pid()} -> ${it.info().command().get()}") }
     */
}