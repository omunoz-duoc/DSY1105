package actividad_1_3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.flow.*

// ================================================== Actividad 1.3.1 ==================================================
fun strToInt(str: String): Result<Int> {
	return runCatching { str.toInt() }
		.recoverCatching {
			throw NumberFormatException("Valor inválido: '$str' no es un número")
		}
}

fun isPositive(number: Int): Result<Int> {
	return if (number < 0) {
		Result.failure(IllegalArgumentException("El número debe ser positivo"))
	} else {
		Result.success(number)
	}
}

//// ================================================== Actividad 1.3.4 ==================================================
fun autenticarUsuario(usuario: String, contrasena: String): Flow<ResultadoLogin> = flow {
	// Emitir estado inicial
	emit(ResultadoLogin.Autenticando)

	try {
		// Validaciones previas
		val validationError = validarCredenciales(usuario, contrasena)
		if (validationError != null) {
			emit(validationError)
			return@flow
		}

		// Autenticacion simulada
		delay(2000L)
		val esValido = verificarCredencialesEnServidor(usuario, contrasena)

		if (esValido) {
			// Obtener datos del usuario
			val perfil = obtenerPerfilUsuario(usuario)
			emit(ResultadoLogin.Exito(perfil))
		} else {
			emit(ResultadoLogin.Error("Credenciales inválidas"))
		}
	} catch (e: NetworkException) {
		emit(ResultadoLogin.Error("Error de red: ${e.message}"))
	} catch (e: TimeoutException) {
		emit(ResultadoLogin.Error("Tiempo de Espera Agotado: ${e.message}"))
	} catch (e: Exception) {
		emit(ResultadoLogin.Error("Error inesperado: ${e.message}"))
	}
}.flowOn(Dispatchers.IO)

fun manejarResultadoLogin(resultado: ResultadoLogin) {
	when (resultado) {
		is ResultadoLogin.Exito -> {
			resultado.perfil.let { perfil ->
				println("Login exitoso. Bienvenido, ${perfil.nombreUsuario}")
				println("Email: ${perfil.email}")
				
				inicializarSesionUsuario(perfil)
				configurarPreferenciasUsuario(perfil)
			}
		}
		is ResultadoLogin.Error -> println("Error: ${resultado.mensaje}")
		is ResultadoLogin.Autenticando -> println("Autenticando...")
	}
}


private fun validarCredenciales(usuario: String, contrasena: String): ResultadoLogin.Error? {
	return when {
		usuario.isBlank() -> ResultadoLogin.Error("El nombre de usuario no puede estar vacío")
		contrasena.isBlank() -> ResultadoLogin.Error("La contraseña no puede estar vacía")
		contrasena.length < 6 -> ResultadoLogin.Error("La contraseña debe tener al menos 6 caracteres")
		!esEmailValido(usuario) -> ResultadoLogin.Error("El formato del email es inválido")
		else -> null
	}
}

private fun verificarCredencialesEnServidor(usuario: String, contrasena: String): Boolean {
	// Simulación de verificación en servidor
	// En un caso real, aquí se haría una llamada a un servicio externo
	return usuario == "admin@admin.cl" && contrasena == "password123"
}

private fun obtenerPerfilUsuario(usuario: String): PerfilUsuario {
	// Simulación de obtención de datos del usuario
	// En un caso real, aquí se haría una llamada a un servicio externo
	return PerfilUsuario(nombreUsuario = "Admin", email = usuario)
}

private fun esEmailValido(email: String): Boolean {
	val emailRegex = "^[A-Za-z](.*)([@]{1})(.+)(\\.)(.+)"
	return Regex(emailRegex).matches(email)
}

private fun inicializarSesionUsuario(perfil: PerfilUsuario) {
	println("   🔧 Inicializando sesión para ${perfil.nombreUsuario}")
}

private fun configurarPreferenciasUsuario(perfil: PerfilUsuario) {
	println("   ⚙️ Configurando preferencias del usuario")
}