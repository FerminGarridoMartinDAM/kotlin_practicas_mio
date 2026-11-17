/*
Este archivo implementa el lado SERVIDOR de una comunicación entre procesos en red (IPC mediante
Sockets TCP/IP), abriendo un puerto de escucha con ServerSocket, aceptando la conexión de un
cliente (Sockets02.kt) y leyendo los mensajes que este le envía.
*/

/*
Puntos clave para estudio de Sockets.kt:

- Diferencia fundamental entre `ServerSocket` y `Socket`:
    - `ServerSocket(9999)`: NO sirve para enviar ni recibir datos directamente. Es únicamente el "recepcionista" o "centralita" que abre el puerto TCP 9999 en nuestra tarjeta de red y se queda esperando a que llamen.
    - `val client: Socket = server.accept()`: El método `.accept()` es **bloqueante** (detiene el programa hasta que un cliente se conecta). En cuanto un cliente entra, `.accept()` crea y devuelve un objeto `Socket` normal dedicado exclusivamente a hablar con ese cliente (`client.inputStream` para leerle y `client.outputStream` para responderle).

- Orden de ejecución obligatorio en tus pruebas con IntelliJ IDEA:
    1. Primero debes dar a "Run" en **`Sockets.kt` (Servidor)**. Verás que imprime `"Esperando conexiones..."` y se queda pausado en `server.accept()`.
    2. Sin parar el servidor, das a "Run" en **`Sockets02.kt` (Cliente)**. En ese instante el servidor se desbloquea, imprime `"Cliente conectado: /127.0.0.1"` y muestra `"Recibido del cliente: Hola"`.
    *(Si ejecutas el cliente antes que el servidor, fallará con `java.net.ConnectException: Connection refused` porque nadie está escuchando en el puerto 9999).*
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones para leer flujos de texto (BufferedReader, InputStreamReader) y escribir (PrintWriter)
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
// Importaciones de red: ServerSocket (para el servidor que escucha) y Socket (para la conexión activa con el cliente)
import java.net.ServerSocket
import java.net.Socket

fun main() {
    // Comentario original de la profesora:
    // Iniciar un servidor de sockets
    // Registra y abre el puerto TCP 9999 en la máquina local.
    // (Ojo: Si ejecutas dos veces el servidor sin cerrar el anterior, lanzará BindException: Address already in use).
    val server = ServerSocket(9999)
    println("Esperando conexiones...")

    // server.accept(): Bloquea la ejecución del servidor hasta que un proceso cliente haga Socket("localhost", 9999).
    // Cuando el cliente se conecta, devuelve un objeto Socket ('client') que representa el canal de comunicación con él.
    val client = server.accept()
    // client.inetAddress: Muestra la dirección IP desde la que se ha conectado el cliente (ej. /127.0.0.1)
    println("Cliente conectado: ${client.inetAddress}")

    // Comentario original de la profesora:
    // Leer datos del cliente
    // Conectamos un BufferedReader al flujo de entrada del socket del cliente (lo que el cliente escribe por red)
    val reader = BufferedReader(InputStreamReader(client.inputStream))
    var line: String?
    // Lee línea a línea los mensajes enviados por el cliente hasta que el cliente cierra su socket (readLine devuelve null)
    while (reader.readLine().also { line = it } != null) {
        println("Recibido del cliente: $line")
    }

    // Comentario original de la profesora:
    // Cerrar conexiones
    // Es buena práctica cerrar siempre en orden inverso: primero los flujos, luego el socket del cliente y por último el ServerSocket
    reader.close()
    client.close()
    server.close()
}