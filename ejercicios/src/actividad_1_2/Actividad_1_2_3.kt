package actividad_1_2

fun main(args: Array<String>) {
	// Declarations
	val calificaciones: List<Int> = listOf(7, 4, 8, 10, 2, 5, 3)
	val nombres: List<String> = listOf("Juan", "Pedro", "Diego")
	val cantFrutas: Map<String, Int> = mapOf("Manzanas" to 100, "Naranjas" to 80)
	
	// Ejercicio 1
	printAveragePassingGrades(calificaciones)
	
	// Ejercicio 2
	printNamesList(nombres)
	printStockReport(cantFrutas)
}

fun printAveragePassingGrades(calificaciones: List<Int>) {
	val aprobados: List<Int> = calificaciones.filter { it >= 4 }
	
	println("El promedio de las notas aprobatorias es ${aprobados.average()}")
}

fun printNamesList(names: List<String>) = names.forEachIndexed { index, nombre -> println("${index + 1}.- $nombre") }

fun printStockReport(productStock: Map<String, Int>) {
	productStock.forEach { (productName, stock) -> println("Hay $stock $productName en stock")}
}