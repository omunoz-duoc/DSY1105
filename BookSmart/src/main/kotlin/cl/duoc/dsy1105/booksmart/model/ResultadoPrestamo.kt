package cl.duoc.dsy1105.booksmart.model

/**
 * Sealed class que representa los posibles resultados de una operación de préstamo.
 * Facilita el manejo de diferentes estados de resultado usando pattern matching.
 */
sealed class ResultadoPrestamo {
	/**
	 * Resultado exitoso con el préstamo creado.
	 */
	data class Exito(val prestamo: Prestamo, val mensaje: String = "Préstamo realizado exitosamente") : ResultadoPrestamo()
	
	/**
	 * Error en el procesamiento del préstamo.
	 */
	data class Error(val mensaje: String, val codigo: String? = null) : ResultadoPrestamo()
	
	/**
	 * Estado de procesamiento en curso.
	 */
	data class Procesando(val progreso: Int = 0, val mensaje: String = "Procesando préstamo...") : ResultadoPrestamo()
	
	/**
	 * Estado de validación previa.
	 */
	data class Validando(val mensaje: String = "Validando datos del préstamo...") : ResultadoPrestamo()
	
	/**
	 * Convierte el resultado a un mensaje legible para el usuario.
	 */
	fun obtenerMensaje(): String {
		return when (this) {
			is Exito -> mensaje
			is Error -> "Error: $mensaje"
			is Procesando -> "$mensaje ($progreso%)"
			is Validando -> mensaje
		}
	}
	
	/**
	 * Verifica si el resultado indica una operación exitosa.
	 */
	fun esExitoso(): Boolean = this is Exito
	
	/**
	 * Verifica si el resultado indica un error.
	 */
	fun esError(): Boolean = this is Error
	
	/**
	 * Verifica si la operación aún está en proceso.
	 */
	fun enProceso(): Boolean = this is Procesando || this is Validando
}