package actividad_1_3

fun main() {
	// Ejemplos Práctico de Programación Orientada a Objetos en Kotlin
	val persona = Persona(
		"Juan Perez",
		"12.345.678-9",
		"01/01/1990",
		34)
	
	val empleado = Empleado(
		"Ana Gomez",
		"98.765.432-1",
		"15/05/1985",
		38,
		"Gerente",
		2500000)
	
	
	val listaPersonas: List<Informable> = listOf(persona, empleado)
	for (individuo in listaPersonas) {
		individuo.informar()
		println("-----")
	}
	
	// Ejemplos Prácticos de control de errores
	strToInt("1d")
		.onSuccess(::println)
		.onFailure { err -> println(err.message) }
	
	isPositive(-5)
		.onSuccess(::println)
		.onFailure { err -> println(err.message) }
	
	isPositive(10)
		.onSuccess(::println)
		.onFailure { err -> println(err.message) }
}

