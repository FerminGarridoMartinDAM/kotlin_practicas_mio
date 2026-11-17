/*
Este archivo implementa el lado "Lector" del sistema de Memoria Compartida (pareja de EscritorMemoriaCompartida.kt).
Accede a la misma región de 1024 bytes en modo de solo lectura (READ_ONLY), recupera primero el tamaño
del mensaje en los primeros 4 bytes y luego extrae exactamente los bytes del texto.
*/

/*
Puntos clave para estudio de LectorMemoriaCompartida.kt:

* Apertura en modo solo lectura (READ y MapMode.READ_ONLY):
    Como este proceso únicamente necesita consultar los datos escritos por el escritor, abre el FileChannel solo con StandardOpenOption.READ y mapea la región con FileChannel.MapMode.READ_ONLY. Si intentara llamar a memoria.put(...) lanzaría una ReadOnlyBufferException.

* Descodificación siguiendo el mismo protocolo binario:
    1. memoria.getInt(0): Lee los 4 primeros bytes (posiciones 0..3) y los interpreta como un Int que indica la longitud exacta del mensaje en bytes.
    2. ByteArray(longitud): Crea un array de bytes en memoria del tamaño exacto que indicó la cabecera.
    3. memoria.get(4, bytes): Copia desde el índice 4 de la memoria compartida tantos bytes como caben en nuestro array 'bytes'.
    4. String(bytes): Convierte los bytes leídos de nuevo a un String legible en UTF-8.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones de Java NIO para abrir el canal y mapear el archivo en modo lectura
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.READ

fun main() {
    // Apunta exactamente al mismo fichero que utilizó el proceso EscritorMemoriaCompartida
    val fichero = Path.of("src/Tema1/memoria_compartida.dat")

    // Abre el canal únicamente con permiso de lectura (READ) y garantiza su cierre al terminar con .use
    FileChannel.open(fichero, READ).use { canal ->
        // Mapea en memoria RAM la misma región de 1024 bytes (desde la posición 0) en modo solo lectura
        val memoria = canal.map(FileChannel.MapMode.READ_ONLY, 0, 1024)

        // Lee el entero (4 bytes) guardado en la posición 0 para saber cuántos bytes mide el mensaje escrito
        val longitud = memoria.getInt(0)

        // Reserva un array de bytes con el tamaño exacto del mensaje
        val bytes = ByteArray(longitud)

        // Extrae los bytes del mensaje empezando en la posición 4 (justo después de los 4 bytes del Int de longitud)
        memoria.get(4, bytes)

        // Convierte el ByteArray a String e imprime el mensaje recuperado por consola
        println("Leído de la memoria compartida: ${String(bytes)}")
    }
}