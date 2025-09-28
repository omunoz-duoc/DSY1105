package actividad_1_2

import kotlinx.serialization.json.Json
import java.text.Normalizer

// ============================================== Actividad 1.2.1 & 1.2.2 ==============================================

/**
 * Realiza una serie de operaciones matemáticas entre n1 y n2 según la lista de operaciones proporcionada.
 * Devuelve una lista con los resultados de cada operación.
 */
fun getOperationResults(n1: Int, n2: Double, operations: List<String>): MutableList<Double> {
	val results = mutableListOf<Double>()
	for (op in operations) {
		results.add(performOperation(n1, n2, op))
	}
	return results
}

/**
 * Realiza una operación matemática entre n1 y n2 según el operador proporcionado.
 * Soporta tanto nombres en español como en inglés para las operaciones.
 */
fun performOperation(n1: Int, n2: Double, op: String): Double {
	return when (op) {
		"suma", "sum" -> n1 + n2
		"resta", "rest" -> n1 - n2
		"multiplicacion", "mult" -> n1 * n2
		"division", "div" -> n1 / n2
		"resto", "modulo", "mod" -> n1 % n2
		else -> throw IllegalArgumentException("Operación no válida")
	}
}

/**
 * Calcula el módulo (resto) de n1 dividido por n2 manualmente.
 * Explicación: El módulo se calcula restando a n1 el producto del cociente entero de n1 dividido por n2 y n2.
 * Esto es útil cuando se trabaja con tipos mixtos (Int y Double) y se desea evitar problemas de precisión.
 */
fun calculateModulo(n1: Int, n2: Double): Double {
	return n1 - (n1 / n2).toInt() * n2
}

/**
 * Verifica si un número es par.
 */
fun isEven(n: Int): Boolean {
	return n % 2 == 0
}


// ================================================== Actividad 1.2.3 ==================================================

/**
 * Verifica si una nota es aprobatoria o no.
 * Una nota aprobatoria es igual o mayor a 4
 */
fun isPassingGrade(grade: Int): Boolean = grade >= 4

/**
 * Agrupa notas por aprobatorias o no aprobatorias
 */
fun groupByPassingGrade(grades: List<Int>): Map<String, List<Int>> {
	return grades.groupBy { if (isPassingGrade(it)) "aprobados" else "reprobados" }
}


// ================================================== Actividad 1.2.4 ==================================================

/**
 * Parsea el archivo JSON de productos y devuelve una lista de objetos Product.
 */
fun getProductsList(): List<Product> {
	// Primero intenta cargarlo desde el classpath (Gradle: src/main/resources)
	val jsonString = ({}.javaClass.getResourceAsStream("/productos.json")
		?: throw IllegalStateException("Resource productos.json no encontrado en el classpath")).bufferedReader().use { it.readText() }
	return Json.decodeFromString<List<Product>>(jsonString)
}

/**
 * Busca un producto por su nombre en una lista de productos.
 * @param name Nombre del producto a buscar.
 * @param products Lista de productos donde buscar.
 * @return El producto si se encuentra, o null si no se encuentra.
 */
fun findProductByName(name: String, products: List<Product>): Product? {
	return products.find { it.nombre.equals(name, ignoreCase = true) }
}

/**
 * Calcula el precio promedio de todos los productos en el inventario. No considera productos sin precio.
 * @param products Lista de productos
 * @return Precio promedio de todos los productos
 */
fun calAveragePrice(products: List<Product>): Double {
	return products.mapNotNull { it.precio }.average()
}

/**
 * Filtra productos por categoria.
 * @param category Categoria por la cual filtrar
 * @param products Lista de productos
 * @return Lista de productos que pertenecen a la categoria dada
 */
fun filterProductsByCategory(category: String, products: List<Product>): List<Product> {
	return products.filter { normalizeAndCompare(it, category) }
}

fun normalizeAndCompare(product: Product, category: String): Boolean {
	val normalizedCat = Normalizer.normalize(product.categoria, Normalizer.Form.NFD)
	val noDiacritics = normalizedCat.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
	return noDiacritics.equals(category, ignoreCase = true)
}