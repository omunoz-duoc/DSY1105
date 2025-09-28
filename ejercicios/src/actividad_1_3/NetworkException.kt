package actividad_1_3

/**
 * Excepción simple para representar errores de red en el módulo de autenticación.
 */
class NetworkException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
