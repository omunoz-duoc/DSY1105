/** GUIA 2 */

package actividad_1_2

fun main(args: Array<String>) {
	// === Variables y Operadores Aritméticos ===
	val n1: Int = 10
	val n2: Double = 3.5
	val operations = listOf("suma", "resta", "multiplicacion", "division", "modulo")
	
	getOperationResults(n1, n2, operations).forEachIndexed { index, result ->
		println("El resultado de la operación ${operations[index]} entre $n1 y $n2 es: $result")
	}
	
	// === Seguridad Ante Nulos (Null Safety) ===
	var nombre: String? = "Aframuz"
	println("El largo del nombre es: ${nombre?.length}")
	nombre = null
	println("El largo del nombre es: ${nombre?.length ?: "Nombre no proporcionado"}")
	
	/*
		En Java, para evitar NullPointerException, se suelen hacer comprobaciones explícitas:
		if (nombre != null) {
		    System.out.println("El largo del nombre es: " + nombre.length());
		} else {
		    System.out.println("Nombre no proporcionado");
		}
	 */
	
	// === Lógica Condicional con when ===
	val dia: Int = 3
	when (dia) {
		1 -> println("Lunes")
		2 -> println("Martes")
		3 -> println("Miércoles")
		4 -> println("Jueves")
		5 -> println("Viernes")
		6 -> println("Sábado")
		7 -> println("Domingo")
		else -> println("Día inválido")
	}
	
	/*
		=========== Preguntas de Reflexión ===========
		1. ¿Qué diferencias y similitudes clave observaste entre Kotlin y Java en esta actividad?
		   R: Kotlin es más conciso y tiene características modernas como la seguridad ante nulos y el uso de 'when' para
		      lógica condicional. Java requiere más código boilerplate y no tiene soporte nativo para null safety. En Java
		      when se reemplaza por switch.
		      
		2. ¿Cómo podrías aplicar lo aprendido sobre la seguridad ante nulos (Null Safety) y la expresión when en futuros
		   proyectos de desarrollo de aplicaciones móviles?
		   R: La seguridad ante nulos ayuda a prevenir errores comunes en tiempo de ejecución, mejorando la estabilidad de
		      la aplicación. La expresión when facilita la lectura y mantenimiento del código al manejar múltiples
		      condiciones de manera clara y concisa.
	 */
}

