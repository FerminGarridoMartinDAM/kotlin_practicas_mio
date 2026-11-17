/*
Este archivo demuestra cómo diseñar una prueba unitaria de concurrencia (multihilo) con JUnit 5
para verificar que una clase compartida (Counter) no sufre condiciones de carrera (Race Conditions)
gracias a la anotación @Synchronized.
*/

/*
Puntos clave para estudio de PruebaUnitariaMultiproceso.kt:

- ¿Qué es una Condición de Carrera (Race Condition) y por qué ocurre en `count++`?
    Aunque `count++` parezca una sola línea, para el procesador son 3 pasos separados:
    1. Leer el valor actual de `count` de la memoria RAM (ej. vale 5).
    2. Sumarle 1 en un registro de la CPU (5 + 1 = 6).
    3. Guardar el 6 de vuelta en la memoria RAM.
    Si 100 hilos hacen esto a la vez SIN `@Synchronized`, dos hilos leen el 5 al mismo tiempo, ambos calculan 6 y ambos guardan 6 (se pierde un incremento). Al final del test, en lugar de dar 100.000, daría un número menor aleatorio (ej. 78.432) y el test fallaría.

- ¿Qué hace `@Synchronized` en Kotlin?
    Es el equivalente en Kotlin a la palabra clave `synchronized` en los métodos de Java.
    Convierte el método `increment()` en una sección crítica protegida por el monitor intrínseco del objeto `Counter`: solo un hilo puede ejecutar `increment()` a la vez, garantizando que el resultado final sea exactamente 100.000 (100 hilos x 1000 incrementos).

- Importancia de `threads.forEach { it.join() }` en el test:
    Si no hacemos `.join()` antes de `assertEquals(100000, counter.getCount())`, el hilo principal de JUnit comprobaría el contador cuando los 100 hilos apenas acaban de arrancar, dando un valor cercano a 0 y fallando el test.
*/

// Define que este archivo pertenece al paquete Tema1
package Tema1

// Importaciones de JUnit 5 para aserciones y marcado de tests
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
// Importa la función helper thread de Kotlin para lanzar hilos
import kotlin.concurrent.thread


/*
Comentario original de la profesora:
En este ejemplo, la clase Counter tiene un mét0do increment que incrementa
 un contador de manera segura utilizando la palabra clave @Synchronized.
 La prueba testConcurrentIncrement crea 100 hilos, cada uno incrementando
 el contador 1000 veces. Al final, se verifica que el valor del contador sea
 100000, asegurando que la concurrencia se maneje correctamente.
 */

class Counter {
    // Estado interno compartido por todos los hilos
    private var count = 0

    // @Synchronized bloquea el monitor del objeto Counter antes de entrar al método y lo libera al salir.
    // Impide que dos hilos ejecuten 'count++' simultáneamente (evita la condición de carrera).
    @Synchronized
    fun increment() {
        count++
    }

    // Devuelve el valor acumulado del contador
    fun getCount(): Int {
        return count
    }
}

class CounterTest {

    @Test
    fun testConcurrentIncrement() {
        // Instancia única del contador que compartirán los 100 hilos
        val counter = Counter()

        // Crea una lista de 100 hilos (índices del 0 al 99) que arrancan inmediatamente en paralelo
        val threads = List(100) {
            thread {
                // Cada uno de los 100 hilos llama 1000 veces al método sincronizado increment()
                repeat(1000) {
                    counter.increment()
                }
            }
        }

        // El hilo del test espera a que los 100 hilos hayan terminado sus 1000 repeticiones cada uno
        threads.forEach { it.join() }

        // Si la sincronización funcionó y no se perdió ningún incremento: 100 * 1000 = 100000
        assertEquals(100000, counter.getCount())
    }
}