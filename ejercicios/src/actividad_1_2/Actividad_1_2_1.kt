package actividad_1_2

fun main(args: Array<String>) {
	// Declara dos variables numéricas (Int o Double) usando val o var, según corresponda.
	val numero1: Int = 10
	var numero2: Double = 20.5
	
	// Realiza al menos tres operaciones matemáticas entre ellas: suma, resta, multiplicación y/o división.
	val operations = listOf("suma", "resta", "multiplicacion", "division", "modulo")
	val operationResults = getOperationResults(numero1, numero2, operations)
	
	// Usa println() para mostrar los resultados en la consola.
	// Todos devuelven un double
	operationResults.forEachIndexed { index, result ->
		println("El resultado de la operación ${operations[index]} entre $numero1 y $numero2 es: $result")
	}
	
	// Genera una funcionalidad que permita verificar si un número es par o no
	val numeroParaVerificar = 7
	println("El número $numeroParaVerificar es ${if (isEven(numeroParaVerificar)) "par" else "impar"}.")
}
