package cl.duoc.dsy1105.booksmart.model

/**
 * Representa un libro físico en la biblioteca.
 * Los libros de referencia no pueden ser prestados (diasPrestamo = 0).
 */
class LibroFisico(
	id: Int,
	titulo: String,
	autor: String,
	categoria: String,
	anioPublicacion: Int,
	precioBase: Int,
	diasPrestamo: Int,
	val cantidadEjemplares: Int = 1,
	val esReferencia: Boolean = false
) : Libro(
	id = id,
	titulo = titulo,
	autor = autor,
	categoria = categoria,
	anioPublicacion = anioPublicacion,
	precioBase = precioBase,
	diasPrestamo = if (esReferencia) 0 else diasPrestamo
) {
	private var ejemplaresDisponibles: Int = cantidadEjemplares
	
	init {
		require(cantidadEjemplares > 0) { "La cantidad de ejemplares debe ser mayor a 0" }
		if (esReferencia) {
			require(diasPrestamo == 0) { "Los libros de referencia no pueden tener días de préstamo" }
		}
	}
	
	/**
	 * Los libros físicos mantienen su precio base sin modificaciones adicionales.
	 */
	override fun costoFinal(): Int = precioBase
	
	/**
	 * Un libro físico está disponible si no es de referencia, tiene ejemplares disponibles
	 * y tiene días de préstamo configurados.
	 */
	override fun estaDisponible(): Boolean {
		return !esReferencia && ejemplaresDisponibles > 0 && diasPrestamo > 0
	}
	
	/**
	 * Intenta prestar un ejemplar del libro. Retorna true si fue exitoso.
	 */
	fun prestarEjemplar(): Boolean {
		return if (estaDisponible()) {
			ejemplaresDisponibles--
			true
		} else {
			false
		}
	}
	
	/**
	 * Devuelve un ejemplar prestado del libro.
	 */
	fun devolverEjemplar() {
		if (ejemplaresDisponibles < cantidadEjemplares) {
			ejemplaresDisponibles++
		}
	}
	
	fun getEjemplaresDisponibles(): Int = ejemplaresDisponibles
	
	override fun descripcion(): String {
		val baseDesc = super.descripcion()
		val refText = if (esReferencia) " (referencia)" else ""
		val disponibilidad = if (esReferencia) "" else " - $ejemplaresDisponibles/$cantidadEjemplares disponibles"
		return "$baseDesc$refText$disponibilidad"
	}
	
	override fun toString(): String {
		return "LibroFisico(id=$id, titulo='$titulo', autor='$autor', " +
				"ejemplares=$ejemplaresDisponibles/$cantidadEjemplares, " +
				"esReferencia=$esReferencia)"
	}
}