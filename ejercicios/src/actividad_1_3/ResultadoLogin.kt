package actividad_1_3

// ================================================== Actividad 1.3.4 ==================================================
// 2. Crear la Jerarquía de Estados
sealed class ResultadoLogin {
	data class Exito(val perfil: PerfilUsuario) : ResultadoLogin()
	data class Error(val mensaje: String) : ResultadoLogin()
	object Autenticando : ResultadoLogin()
}