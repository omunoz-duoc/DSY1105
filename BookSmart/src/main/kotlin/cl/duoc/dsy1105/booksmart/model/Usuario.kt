package cl.duoc.dsy1105.booksmart.model

/**
 * Representa un usuario del sistema BookSmart.
 * El tipo de usuario se determina automáticamente basado en su email.
 */
data class Usuario(
	val id: Int,
	val nombre: String,
	val email: String,
	val contrasena: String,
	private var cantidadAtrasos: Int = 0,
	val tipoUsuario: TipoUsuario = TipoUsuario.determinarTipoUsuario(email)
) {
	
	init {
		require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
		require(email.isNotBlank()) { "El email no puede estar vacío" }
		require(contrasena.isNotBlank()) { "La contraseña no puede estar vacía" }
		require(cantidadAtrasos >= 0) { "La cantidad de atrasos no puede ser negativa" }
		require(esEmailValido(email)) { "El formato del email no es válido: $email" }
	}
	
	/**
	 * Verifica si el usuario puede realizar préstamos.
	 * Los usuarios con muchos atrasos podrían tener restricciones.
	 */
	fun puedeRealizarPrestamos(): Boolean {
		return cantidadAtrasos < 3 // Máximo 3 atrasos permitidos
	}
	
	/**
	 * Incrementa el contador de atrasos del usuario.
	 */
	fun registrarAtraso() {
		cantidadAtrasos++
	}
	
	/**
	 * Reinicia el contador de atrasos (por ejemplo, después de un período sin atrasos).
	 */
	fun reiniciarAtrasos() {
		cantidadAtrasos = 0
	}
	
	fun getCantidadAtrasos(): Int = cantidadAtrasos
	
	/**
	 * Verifica si las credenciales proporcionadas coinciden con este usuario.
	 */
	fun verificarCredenciales(email: String, contrasena: String): Boolean {
		return this.email == email && this.contrasena == contrasena
	}
	
	/**
	 * Calcula el descuento que aplica para este usuario.
	 */
	fun calcularDescuento(precioBase: Int): Int {
		return tipoUsuario.calcularDescuento(precioBase)
	}
	
	/**
	 * Aplica el descuento correspondiente al tipo de usuario.
	 */
	fun aplicarDescuento(precioBase: Int): Int {
		return tipoUsuario.aplicarDescuento(precioBase)
	}
	
	private fun esEmailValido(email: String): Boolean {
		return email.contains("@") && email.contains(".")
	}
	
	override fun toString(): String {
		return "Usuario(id=$id, nombre='$nombre', email='$email', tipo=${tipoUsuario.descripcion})"
	}
}