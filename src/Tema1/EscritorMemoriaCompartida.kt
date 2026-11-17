/*
Este archivo implementa el lado "Escritor" de un sistema de Comunicación Entre Procesos (IPC)
basado en Memoria Compartida (Shared Memory), proyectando un archivo en la memoria RAM
mediante MappedByteBuffer (canal.map).
*/

/*
Puntos clave para estudio de EscritorMemoriaCompartida.kt:

* ¿Qué es un archivo mapeado en memoria (Memory-Mapped File / MappedByteBuffer)?
    Mediante canal.map(FileChannel.MapMode.READ_WRITE, 0, 1024), el Sistema Operativo reserva una región de 1024 bytes de memoria RAM virtual directamente vinculada al fichero "memoria_compartida.dat".
    Cuando varios procesos mapean el mismo archivo, comparten directamente esas mismas páginas de memoria RAM física. Es el mecanismo de IPC más rápido porque evita copiar datos continuamente entre el espacio de usuario y el kernel.

* Permisos obligatorios en FileChannel.open para MapMode.READ_WRITE:
    Para poder mapear en modo READ_WRITE, es OBLIGATORIO abrir el FileChannel incluyendo tanto READ como WRITE (además de CREATE por si el archivo aún no existe). Si solo pusiéramos WRITE, canal.map() lanzaría una NonReadableChannelException.

* Diseño del protocolo de datos en el buffer (Offset 0 y Offset 4):
    Como la zona de memoria mide siempre 1024 bytes fijos, el proceso lector necesita saber cuántos bytes reales ocupa nuestro mensaje para no leer basura o bytes vacíos (ceros) hasta el byte 1024:
    - Posición (offset) 0: Escribimos un entero (Int) con la longitud del mensaje (memoria.putInt(0, mensaje.size)). Como un Int en Java/Kotlin ocupa exactamente 4 bytes (32 bits), ocupa las posiciones 0, 1, 2 y 3.
    - Posición (offset) 4: Justo a continuación del Int (en el byte índice 4), escribimos los bytes del texto (memoria.put(4, mensaje)).
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones de Java NIO para canales de fichero, rutas y opciones de apertura
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*

fun main() {
    // Ruta del archivo binario que servirá de soporte para la memoria compartida entre procesos
    val fichero = Path.of("src/Tema1/memoria_compartida.dat")

    // Abre el canal con permisos de creación, lectura y escritura, cerrándolo automáticamente al salir del bloque .use
    FileChannel.open(fichero, CREATE, READ, WRITE).use { canal ->
        // Mapear 1024 bytes del fichero en memoria (desde el byte 0 hasta el 1024) en modo Lectura/Escritura.
        // Devuelve un objeto MappedByteBuffer llamado 'memoria'.
        val memoria = canal.map(FileChannel.MapMode.READ_WRITE, 0, 1024)

        // Convierte el String del mensaje en un array de bytes (en codificación UTF-8 por defecto)
        val mensaje = "Hola desde la memoria compartida".toByteArray()

        // Escribe en los primeros 4 bytes (índices 0, 1, 2 y 3) el número de bytes que mide el mensaje
        memoria.putInt(0, mensaje.size)     // Primero la longitud del mensaje

        // Escribe a partir del byte índice 4 el contenido real del array de bytes del mensaje
        memoria.put(4, mensaje)             // Después el contenido

        println("Mensaje escrito en la memoria compartida")
    }
}