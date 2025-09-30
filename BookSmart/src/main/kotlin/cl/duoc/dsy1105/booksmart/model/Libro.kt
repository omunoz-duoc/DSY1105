package cl.duoc.dsy1105.booksmart.model

/**
 * Clase base abstracta que representa un libro en el sistema BookSmart.
 * Implementa los atributos y funcionalidades comunes a todos los tipos de libros.
 */
open class Libro(
	val id: Int,
	val titulo: String,
	val autor: String,
	val categoria: String,
	val anioPublicacion: Int,
	val precioBase: Int,
	val diasPrestamo: Int
) {
	init {
		// Validaciones en el constructor
		require(precioBase >= 0) { "El precio base no puede ser negativo: $precioBase" }
		require(diasPrestamo >= 0) { "Los días de préstamo no pueden ser negativos: $diasPrestamo" }
		require(titulo.isNotBlank()) { "El título no puede estar vacío" }
		require(autor.isNotBlank()) { "El autor no puede estar vacío" }
	}
	
	/**
	 * Calcula el costo final del libro. Puede ser sobrescrito por las clases hijas
	 * para aplicar lógica específica según el tipo de libro.
	 */
	open fun costoFinal(): Int = precioBase
	
	/**
	 * Verifica si el libro está disponible para préstamo.
	 * Debe ser implementado por las clases hijas según su lógica específica.
	 */
	open fun estaDisponible(): Boolean = diasPrestamo > 0
	
	/**
	 * Genera una descripción del libro que puede incluir información adicional
	 * específica del tipo de libro.
	 */
	open fun descripcion(): String {
		return "$titulo por $autor ($anioPublicacion)"
	}
	
	override fun toString(): String {
		return "Libro(id=$id, titulo='$titulo', autor='$autor', categoria='$categoria', " +
				"año=$anioPublicacion, precio=$precioBase, días=$diasPrestamo)"
	}
}