/*
Este archivo introduce el concepto de Pruebas de Integración (Integration Testing) utilizando
JUnit 5 para verificar la comunicación e interacción entre dos componentes o procesos simulados
(ProcessA y ProcessB).
*/

/*
Puntos clave para estudio de PruebasIntegracion.kt:

- Diferencia de examen entre Prueba Unitaria y Prueba de Integración:
    - Prueba Unitaria (Unit Test): Prueba un único método o clase de forma totalmente aislada (por ejemplo, probar que el método `sumar(2, 2)` devuelve `4`).
    - Prueba de Integración (Integration Test): Prueba que varios módulos, clases o procesos separados funcionen correctamente cuando se conectan entre sí (por ejemplo, que el Proceso A envíe un dato y el Proceso B lo reciba).

- ¡OJO! Detalle crítico si ejecutas este test en IntelliJ IDEA (Fallo intencionado o incompleto en el código original):
    Si le das al botón "Run Test" tal y como está escrito el archivo original, el test FALLARÁ con:
    `AssertionFailedError: expected: <Hello from A> but was: <null>`
    ¿Por qué? Porque `ProcessA` guarda el texto en su propia variable privada `message`, pero nunca se lo pasa a `ProcessB` (la variable `message` de `ProcessB` sigue siendo `null`).
    Para que el test pase en verde si te lo piden corregir en clase, `ProcessA` tendría que enviarle el mensaje a `ProcessB` (por ejemplo: `fun sendMessage(msg: String, destino: ProcessB) { destino.recibir(msg) }`) o compartir un canal común.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa assertEquals de JUnit 5 (Jupiter) para comprobar que el valor obtenido coincide con el esperado
import org.junit.jupiter.api.Assertions.assertEquals
// Importa la anotación @Test que marca un método como ejecutable por el motor de pruebas de JUnit
import org.junit.jupiter.api.Test

/*
Comentario original de la profesora:
En este ejemplo, tenemos dos procesos simulados, ProcessA y ProcessB.
La prueba de integración testProcessCommunication verifica que ProcessA
pueda enviar un mensaje a ProcessB y que ProcessB lo reciba correctamente.
 */
class IntegrationTest {

    // @Test indica a JUnit e IntelliJ IDEA que esta función es una prueba automatizada (aparecerá el icono de Play verde al lado)
    @Test
    fun testProcessCommunication() {
        // 1. Fase Arrange (Preparación): Instanciamos e iniciamos ambos procesos simulados
        val processA = ProcessA()
        val processB = ProcessB()

        processA.start()
        processB.start()

        // 2. Fase Act (Acción): El proceso A envía el mensaje
        processA.sendMessage("Hello from A")

        // 3. Fase Assert (Verificación): Comprobamos que lo que recibe B sea exactamente "Hello from A"
        // Primer argumento: valor esperado ("Hello from A"). Segundo argumento: valor real obtenido (processB.receiveMessage()).
        // (Nota: Como en el código original ProcessA y ProcessB no están conectados, processB.receiveMessage() devuelve null y el test falla aquí).
        assertEquals("Hello from A", processB.receiveMessage())
    }
}

// Clase que simula el proceso emisor
class ProcessA {
    // Variable privada donde ProcessA almacena internamente el mensaje
    private var message: String? = null

    fun start() {
        // Comentario original de la profesora:
        // Simulación de inicio del proceso
    }

    // Guarda el mensaje en la propiedad interna de ProcessA
    fun sendMessage(msg: String) {
        message = msg
    }
}

// Clase que simula el proceso receptor
class ProcessB {
    // Variable privada de ProcessB (inicializada a null y nunca modificada en este esqueleto)
    private var message: String? = null

    fun start() {
        // Comentario original de la profesora:
        // Simulación de inicio del proceso
    }

    // Devuelve el contenido del mensaje de ProcessB (devolverá null a menos que se conecte con ProcessA)
    fun receiveMessage(): String? {
        return message
    }
}