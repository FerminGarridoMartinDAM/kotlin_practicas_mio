/*
Este archivo muestra cómo sincronizar varios procesos independientes que intentan escribir
en un mismo archivo compartido (registro_compartido.txt) utilizando bloqueos de fichero
a nivel de Sistema Operativo mediante FileChannel y FileLock (API Java NIO).
*/

/*
Puntos clave para estudio de BloqueoDeFicheros.kt:

* ¿Por qué usar FileChannel.lock() en lugar de ReentrantLock o @Synchronized?
    ReentrantLock, Semaphore o @Synchronized solo sincronizan HILOS dentro de una misma JVM (un único proceso).
    Si lanzamos dos programas independientes (dos procesos con distinta JVM) que escriben a la vez en el mismo fichero, necesitamos un mecanismo a nivel de Sistema Operativo: FileChannel.lock().

* Opciones de apertura (StandardOpenOption):
    - CREATE: Crea el fichero si aún no existe.
    - WRITE: Abre el canal con permisos de escritura.
    - APPEND: Garantiza que cada escritura se sitúe al final del archivo sin machacar lo anterior.

* Gestión automática de recursos con .use { ... } en Kotlin:
    La función .use { ... } es el equivalente en Kotlin al try-with-resources de Java.
    Se aplica tanto al FileChannel como al FileLock devuelto por canal.lock().
    Garantiza que, al terminar el bloque (o incluso si ocurre una excepción en medio de la escritura), el cerrojo (FileLock) se libere automáticamente y el canal se cierre, evitando dejar el archivo bloqueado permanentemente.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa FileChannel (Java NIO) para operar con canales de ficheros que soportan bloqueo y mapeo en memoria
import java.nio.channels.FileChannel
// Importa Path para representar rutas del sistema de archivos de forma moderna (NIO.2)
import java.nio.file.Path
// Importa todas las constantes de StandardOpenOption (CREATE, WRITE, APPEND, READ...)
import java.nio.file.StandardOpenOption.*

fun main() {
    // Obtiene el identificador de proceso (PID) del proceso actual en el sistema operativo para distinguirlo en el log
    val pid = ProcessHandle.current().pid()

    // Define la ruta relativa hacia el archivo de texto que compartirán los procesos
    val fichero = Path.of("src/Tema1/registro_compartido.txt")

    // Abre el canal sobre el fichero (creándolo si no existe, en modo escritura y añadiendo al final).
    // El bloque .use { canal -> ... } cerrará el FileChannel automáticamente al finalizar.
    FileChannel.open(fichero, CREATE, WRITE, APPEND).use { canal ->
        println("[$pid] Esperando el bloqueo del fichero...")

        // canal.lock() solicita al Sistema Operativo un bloqueo exclusivo sobre todo el archivo.
        // Si otro proceso tiene el bloqueo en este instante, esta llamada detiene (bloquea) al proceso actual hasta que el otro lo suelte.
        // El segundo .use { ... } asegura que el objeto FileLock se cierre (liberando el bloqueo) al salir de las llaves.
        canal.lock().use { // Bloquea hasta que ningún otro proceso tenga el lock
            println("[$pid] Bloqueo obtenido, escribiendo...")

            // En Java NIO, FileChannel no escribe Strings directamente, sino objetos ByteBuffer.
            // Convertimos el String a un array de bytes (.toByteArray()) y lo envolvemos con ByteBuffer.wrap(...) para escribirlo.
            canal.write(java.nio.ByteBuffer.wrap("Línea escrita por el proceso $pid\n".toByteArray()))

            // Pausa de 3 segundos mientras mantenemos el bloqueo retenido.
            // Esto permite ejecutar una segunda instancia del programa desde el IDE al mismo tiempo y comprobar cómo la segunda espera.
            Thread.sleep(3000) // Simula una escritura larga
        } // El bloqueo se libera al salir del bloque

        println("[$pid] Bloqueo liberado")
    }
}