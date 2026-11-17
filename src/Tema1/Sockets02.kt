/*
Este archivo implementa el lado CLIENTE de la comunicación por Sockets TCP/IP (pareja de Sockets.kt),
conectándose al puerto 9999 del servidor local y enviándole un mensaje de texto.
*/

/*
Puntos clave para estudio de Sockets02.kt:

- Los dos parámetros de `Socket("localhost", 9999)`:
    1. Host (`"localhost"` o `"127.0.0.1"`): Dirección IP o nombre de dominio de la máquina donde está corriendo el `ServerSocket`. Como el servidor y el cliente están en nuestro mismo PC, usamos `"localhost"`.
    2. Puerto (`9999`): Debe coincidir exactamente con el número de puerto que abrió el `ServerSocket(9999)` en `Sockets.kt`.

- Dos detalles críticos de examen sobre `PrintWriter(socket.getOutputStream(), true)` y `writer.println("Hola")`:
    1. ¿Por qué el segundo argumento de `PrintWriter` es `true` (`autoFlush = true`)?
       Porque los flujos de red usan un buffer intermedio. Con `true`, cada vez que llamas a `println(...)`, el `PrintWriter` hace un `.flush()` automático enviando los paquetes de red al instante. Si fuera `false` (valor por defecto), el mensaje podría quedarse retenido en la memoria del cliente sin salir por la red hasta llenar el buffer o cerrar el socket.
    2. ¿Por qué es obligatorio usar `writer.println("Hola")` y no `writer.print("Hola")`?
       Porque en `Sockets.kt` el servidor está leyendo con `reader.readLine()`, que busca un salto de línea (`\n`) para saber dónde termina el mensaje. Además, el `autoFlush` de `PrintWriter` solo se dispara con `println`, `printf` o `format`, nunca con `print`.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa PrintWriter para enviar texto formateado fácilmente a través del flujo de salida del socket
import java.io.PrintWriter
// Importa Socket para establecer la conexión TCP como cliente
import java.net.Socket

fun main() {
    // Intenta conectarse inmediatamente al servidor en la máquina local ("localhost") y puerto 9999.
    // Si el servidor está esperando en server.accept(), aquí se completa el "Three-Way Handshake" de TCP.
    val socket = Socket("localhost", 9999)

    // Envuelve el OutputStream del socket en un PrintWriter.
    // El segundo parámetro 'true' activa el autoFlush para que los datos se envíen inmediatamente al hacer println().
    val writer = PrintWriter(socket.getOutputStream(), true)

    // Envía la cadena "Hola" seguida de un salto de línea (\n) hacia el servidor y vacía el buffer automáticamente
    writer.println("Hola")

    // Cierra el escritor y el socket del cliente (esto envía un paquete FIN de TCP al servidor,
    // haciendo que el bucle while(reader.readLine() != null) del servidor reciba null y termine limpiamente)
    writer.close()
    socket.close()
}