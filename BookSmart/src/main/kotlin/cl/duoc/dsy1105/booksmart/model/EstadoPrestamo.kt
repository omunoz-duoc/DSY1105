package cl.duoc.dsy1105.booksmart.model

/**
 * Sealed class que representa los diferentes estados posibles de un préstamo.
 * Permite un manejo seguro de estados usando pattern matching con when.
 */
sealed class EstadoPrestamo {
	/**
	 * Estado inicial cuando se solicita un préstamo pero aún no se ha procesado.
	 */
	object Pendiente : EstadoPrestamo()
	
	/**
	 * Estado cuando el libro está actualmente prestado al usuario.
	 */
	data class EnPrestamo(val diasRestantes: Int) : EstadoPrestamo()
	
	/**
	 * Estado cuando el libro ha sido devuelto exitosamente.
	 */
	object Devuelto : EstadoPrestamo()
	
	/**
	 * Estado que indica que ocurrió un error durante el proceso.
	 */
	data class Error(val mensaje: String) : EstadoPrestamo()
	
	/**
	 * Retorna una descripción legible del estado actual.
	 */
	fun descripcion(): String {
		return when (this) {
			is Pendiente -> "Préstamo pendiente de procesamiento"
			is EnPrestamo -> "En préstamo - $diasRestantes días restantes"
			is Devuelto -> "Libro devuelto"
			is Error -> "Error: $mensaje"
		}
	}
	
	/**
	 * Verifica si el estado permite realizar acciones sobre el préstamo.
	 */
	fun esActivo(): Boolean {
		return when (this) {
			is Pendiente, is EnPrestamo -> true
			is Devuelto, is Error -> false
		}
	}
}