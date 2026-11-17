/*
Este archivo introduce el registro de eventos (Logging) seguro en entornos concurrentes
mediante la API estándar java.util.logging, escribiendo las trazas de múltiples hilos
en un fichero compartido (app.log).
*/

/*
Puntos clave para estudio de Logging.kt:

- ¿Por qué usar Logger en lugar de println() o escribir directamente con File.appendText()?
    1. Thread-Safety (Seguridad entre hilos): Las clases Logger y FileHandler están sincronizadas internamente. Si 5 hilos intentan registrar un evento a la vez, las líneas no se mezclan ni se corrompen en el archivo.
    2. Persistencia y auditoría: Lo que se imprime con println() se pierde al cerrar la consola; el FileHandler deja un historial con fecha, hora, nivel de gravedad (INFO, WARNING, SEVERE) y método de origen.

- Detalle importante de examen sobre Formateadores (Formatter):
    Por defecto, un FileHandler escribe los logs en formato XML (XMLFormatter).
    Por eso es imprescindible asignarle un SimpleFormatter() si queremos leer el archivo app.log como texto plano legible.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa FileHandler: manejador que redirige los mensajes del Logger hacia un archivo físico en disco
import java.util.logging.FileHandler
// Importa Logger: clase principal encargada de capturar y categorizar los mensajes de registro
import java.util.logging.Logger
// Importa SimpleFormatter: formatea los registros de log como texto plano legible en lugar de XML
import java.util.logging.SimpleFormatter
// Importa la función helper thread {} de Kotlin para crear e iniciar hilos de forma concisa
import kotlin.concurrent.thread

fun main() {
    // Obtiene (o crea si no existe) una instancia de Logger identificada con el nombre "MyLogger"
    val logger = Logger.getLogger("MyLogger")

    // Crea un manejador de fichero que escribirá en "app.log" (en la raíz del proyecto).
    // El segundo parámetro 'true' (append) indica que NO sobrescriba el fichero al arrancar, sino que añada al final.
    val fileHandler = FileHandler("app.log", true)

    // Cambia el formateador por defecto (que sería XMLFormatter) a SimpleFormatter (texto plano con fecha, nivel y mensaje)
    fileHandler.formatter = SimpleFormatter()

    // Conecta el manejador de archivo al logger (a partir de ahora, los mensajes saldrán por consola y también se guardarán en app.log)
    logger.addHandler(fileHandler)

    // Comentario original de la profesora:
    // Simulación de procesos concurrentes
    // Bucle que se repite 5 veces (con 'i' tomando valores del 0 al 4) para lanzar 5 hilos en paralelo
    repeat(5) { i ->
        // Crea e inicia inmediatamente un nuevo hilo de ejecución
        thread {
            // Registra un mensaje con nivel informativo (Level.INFO) indicando que el hilo 'i' ha comenzado
            logger.info("Iniciando proceso $i")

            // Pausa el hilo actual durante un tiempo aleatorio entre 1000 ms (1 seg) y 3000 ms (3 seg) para simular carga de trabajo
            Thread.sleep((1000..3000).random().toLong())

            // Registra el evento de finalización; como el tiempo es aleatorio, terminarán en un orden distinto al que empezaron
            logger.info("Finalizando proceso $i")
        }
    }
}