/** GUIA 4 */

package actividad_1_3

// 1. Creación de la Clase Base Persona
open class Persona2(val nombre: String, val edad: Int) {
	open fun presentarse() {
		println("Hola, mi nombre es $nombre.")
	}
}

// 2. Implementación de Herencia con la Clase Empleado
class Empleado2(nombre: String, edad: Int, var puesto: String, private var salario: Double) : Persona2(nombre, edad) {

	fun mostrarPuesto() {
		println("$nombre trabaja como $puesto.")
	}
	
	// 3. Aplicación de Polimorfismo
	override fun presentarse() {
		println("Hola, soy $nombre y mi puesto es $puesto.")
	}
}


// 4. Creación de Objetos y Verificación
fun main() {
	val persona = Persona2("Carlos", 30)
	val empleado = Empleado2("María", 28, "Desarrolladora", 3000.0)
	
	persona.presentarse()
	empleado.presentarse()
}

/*
	Pregunta de Reflexión:
	 Explica por qué la misma llamada al método 'presentarse()' puede generar dos resultados distintos.
	 R: La misma llamada al método presentarse() genera dos resultados distintos debido al polimorfismo, específicamente
	    al polimorfismo de sobrescritura (override).

	 
	 ¿Qué concepto de la POO se está demostrando aquí? Argumenta con ejemplos
	 R: Polimorfismo de sobrescritura (Override) - permite que objetos de diferentes clases respondan de manera específica
	    al mismo mensaje, manteniendo una interfaz común. Ejemplo: El método presentarse() en la clase base Persona2 y su
	    sobrescritura en la clase derivada Empleado2.
*/