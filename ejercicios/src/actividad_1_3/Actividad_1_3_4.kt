package actividad_1_3

import kotlinx.coroutines.runBlocking

fun main (args: Array<String>) = runBlocking {
	
	// Caso 1: Login exitoso
	println("Caso 1: Credenciales correctas")
	autenticarUsuario("admin@admin.cl", "password123")
		.collect { resultado ->
			manejarResultadoLogin(resultado)
		}
	
	println("\n" + "=".repeat(40) + "\n")
	
	// Caso 2: Login fallido
	println("Caso 2: Credenciales incorrectas")
	autenticarUsuario("usuario@test.com", "wrongpass")
		.collect { resultado ->
			manejarResultadoLogin(resultado)
		}
	
	println("\n" + "=".repeat(40) + "\n")
	
	// Caso 3: Validación fallida
	println("Caso 3: Email inválido")
	autenticarUsuario("email-invalido", "123456")
		.collect { resultado ->
			manejarResultadoLogin(resultado)
		}
}