package cl.duoc.dsy1105.booksmart.exception

import cl.duoc.dsy1105.booksmart.util.Constants

/**
 * Excepción personalizada para errores de validación en el sistema BookSmart.
 * Se lanza cuando los datos ingresados no cumplen con las reglas de negocio.
 */
class ValidacionException(
    message: String,
    val codigo: String = Constants.ErrorCodes.VALIDACION_FALLIDA,
    val campo: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Constructor para validaciones de campo específico.
     */
    constructor(campo: String, valorInvalido: Any?, motivo: String) : this(
        message = "Error en campo '$campo': $motivo. Valor recibido: $valorInvalido",
        campo = campo
    )
    
    /**
     * Constructor para validaciones de rango numérico.
     */
    constructor(campo: String, valor: Number, min: Number, max: Number) : this(
        message = "El campo '$campo' debe estar entre $min y $max. Valor actual: $valor",
        campo = campo
    )
    
    /**
     * Constructor para validaciones de longitud de texto.
     */
    constructor(campo: String, texto: String, longitudMinima: Int, longitudMaxima: Int) : this(
        message = "El campo '$campo' debe tener entre $longitudMinima y $longitudMaxima caracteres. Longitud actual: ${texto.length}",
        campo = campo
    )
    
    override fun toString(): String {
        val campoInfo = campo?.let { " [Campo: $it]" } ?: ""
        return "ValidacionException: $message$campoInfo [Código: $codigo]"
    }
}