package cl.duoc.dsy1105.booksmart.model

/**
 * Enumeration que define los tipos de usuarios del sistema y sus descuentos correspondientes.
 * Según los requisitos:
 * - Estudiantes (@duocuc.cl): 10% descuento
 * - Docentes (@duoc.cl): 15% descuento 
 * - Externos (otros): 0% descuento
 */
enum class TipoUsuario(val descuento: Double, val descripcion: String) {
	ESTUDIANTE(0.10, "Estudiante DUOC UC"),
	DOCENTE(0.15, "Docente DUOC UC"),
	EXTERNO(0.0, "Usuario Externo"),
	ADMIN(0.0, "Administrador del Sistema");
	
	/**
	 * Determina el tipo de usuario basado en su email.
	 */
	companion object {
		fun determinarTipoUsuario(email: String): TipoUsuario {
			return when {
				email.endsWith("@duocuc.cl") -> ESTUDIANTE
				email.endsWith("@duoc.cl") -> DOCENTE
				email == "admin@booksmart.com" -> ADMIN
				else -> EXTERNO
			}
		}
	}
	
	/**
	 * Calcula el monto del descuento para un precio dado.
	 */
	fun calcularDescuento(precioBase: Int): Int {
		return (precioBase * descuento).toInt()
	}
	
	/**
	 * Calcula el precio final después de aplicar el descuento.
	 */
	fun aplicarDescuento(precioBase: Int): Int {
		return precioBase - calcularDescuento(precioBase)
	}
}