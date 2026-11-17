/*
Este archivo muestra la estructura mínima de una Prueba Unitaria (Unit Test) en Kotlin
utilizando el framework JUnit 5 (Jupiter).
*/

/*
Puntos clave para estudio de PruebaUnitaria.kt:

- Anatomía de un método en JUnit 5:
    1. `@Test`: Anotación obligatoria encima de la función. Sin ella, JUnit ignora el método. Los métodos `@Test` no pueden ser `private` ni devolver ningún valor (`Unit`).
    2. `assertEquals(esperado, actual)`: Evalúa si el resultado es correcto.
       - Si `esperado == actual` (4 == 4): El test pasa (icono verde en IntelliJ).
       - Si `esperado != actual`: Lanza un `AssertionFailedError`, el test se marca como fallido (icono rojo/amarillo) y muestra qué valor esperaba y cuál recibió.

- Nota sobre el comentario del archivo original:
    El comentario de cabecera menciona una clase `Calculator` sumando `2 y 3 = 5`, pero en el código se ha simplificado directamente a `assertEquals(4, 2 + 2)` para no necesitar una clase externa.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importa el método estático assertEquals de la librería JUnit 5 (org.junit.jupiter.api)
import org.junit.jupiter.api.Assertions.assertEquals
// Importa la anotación @Test de JUnit 5
import org.junit.jupiter.api.Test

/* Comentario original de la profesora:
En este ejemplo, estamos probando una clase Calculator que tiene
un mét0do add. La prueba verifica que la suma de 2 y 3 sea igual a 5.
Para ejecutar las pruebas unitarias, podemos utilizar un entorno de desarrollo
 integrado (IDE) como IntelliJ IDEA, que tiene soporte integrado para JUnit.
También podemos ejecutar las pruebas desde la línea de comandos utilizando Gradle o Maven.
 */
class CalculatorTest {

    // Marca la función 'suma' como un caso de prueba unitaria
    @Test
    fun suma() {
        // Verifica que el resultado esperado (primer parámetro: 4) sea igual al resultado de la operación (segundo parámetro: 2 + 2)
        assertEquals(4, 2 + 2)

    }
}