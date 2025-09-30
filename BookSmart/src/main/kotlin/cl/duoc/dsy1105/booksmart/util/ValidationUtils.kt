package cl.duoc.dsy1105.booksmart.util

import cl.duoc.dsy1105.booksmart.exception.ValidacionException
import cl.duoc.dsy1105.booksmart.model.*

/**
 * Utilidades para validación de datos en el sistema BookSmart.
 * Proporciona métodos de validación reutilizables con manejo de errores.
 */
object ValidationUtils {
    
    /**
     * Valida que un string no esté vacío o en blanco.
     */
    fun validarTextoNoVacio(texto: String, campo: String): String {
        if (texto.isBlank()) {
            throw ValidacionException("El campo '$campo' no puede estar vacío")
        }
        return texto.trim()
    }
    
    /**
     * Valida la longitud de un texto.
     */
    fun validarLongitudTexto(texto: String, campo: String, minLength: Int, maxLength: Int): String {
        val textoLimpio = validarTextoNoVacio(texto, campo)
        if (textoLimpio.length < minLength || textoLimpio.length > maxLength) {
            throw ValidacionException(
                "El campo '$campo' debe tener entre $minLength y $maxLength caracteres. " +
                "Actual: ${textoLimpio.length}"
            )
        }
        return textoLimpio
    }
    
    /**
     * Valida el formato de un email.
     */
    fun validarEmail(email: String): String {
        val emailLimpio = validarTextoNoVacio(email, "Email")
        if (!Constants.Validaciones.REGEX_EMAIL.matches(emailLimpio)) {
            throw ValidacionException("El formato del email no es válido: $emailLimpio")
        }
        return emailLimpio.lowercase()
    }
    
    /**
     * Valida que un número esté en un rango específico.
     */
    fun validarRango(numero: Int, campo: String, min: Int, max: Int): Int {
        if (numero < min || numero > max) {
            throw ValidacionException(
                "El campo '$campo' debe estar entre $min y $max. Actual: $numero"
            )
        }
        return numero
    }
    
    /**
     * Valida que un precio sea válido (no negativo).
     */
    fun validarPrecio(precio: Int): Int {
        return validarRango(
            precio, 
            "Precio", 
            Constants.PRECIO_MINIMO_LIBRO, 
            Constants.PRECIO_MAXIMO_LIBRO
        )
    }
    
    /**
     * Valida los días de préstamo.
     */
    fun validarDiasPrestamo(dias: Int): Int {
        return validarRango(dias, "Días de préstamo", 0, Constants.MAX_DIAS_PRESTAMO)
    }
    
    /**
     * Valida los datos de un libro.
     */
    fun validarDatosLibro(
        titulo: String,
        autor: String,
        categoria: String,
        anioPublicacion: Int,
        precioBase: Int,
        diasPrestamo: Int
    ): LibroValidado {
        val tituloValido = validarLongitudTexto(
            titulo, 
            "Título", 
            Constants.Validaciones.MIN_LENGTH_TITULO, 
            Constants.Validaciones.MAX_LENGTH_TITULO
        )
        
        val autorValido = validarLongitudTexto(
            autor, 
            "Autor", 
            Constants.Validaciones.MIN_LENGTH_NOMBRE, 
            Constants.Validaciones.MAX_LENGTH_NOMBRE
        )
        
        val categoriaValida = validarTextoNoVacio(categoria, "Categoría")
        
        val anioValido = validarRango(anioPublicacion, "Año de publicación", 1000, 2030)
        val precioValido = validarPrecio(precioBase)
        val diasValidos = validarDiasPrestamo(diasPrestamo)
        
        return LibroValidado(
            titulo = tituloValido,
            autor = autorValido,
            categoria = categoriaValida,
            anioPublicacion = anioValido,
            precioBase = precioValido,
            diasPrestamo = diasValidos
        )
    }
    
    /**
     * Valida los datos de un usuario.
     */
    fun validarDatosUsuario(
        nombre: String,
        email: String,
        contrasena: String
    ): UsuarioValidado {
        val nombreValido = validarLongitudTexto(
            nombre, 
            "Nombre", 
            Constants.Validaciones.MIN_LENGTH_NOMBRE, 
            Constants.Validaciones.MAX_LENGTH_NOMBRE
        )
        
        val emailValido = validarEmail(email)
        
        val contrasenaValida = validarLongitudTexto(
            contrasena, 
            "Contraseña", 
            Constants.Validaciones.MIN_LENGTH_PASSWORD, 
            Constants.Validaciones.MAX_LENGTH_PASSWORD
        )
        
        return UsuarioValidado(
            nombre = nombreValido,
            email = emailValido,
            contrasena = contrasenaValida,
            tipoUsuario = TipoUsuario.determinarTipoUsuario(emailValido)
        )
    }
    
    /**
     * Valida si un libro puede ser prestado.
     */
    fun validarPrestamoLibro(libro: Libro): Boolean {
        when {
            !libro.estaDisponible() -> {
                throw ValidacionException(Constants.Mensajes.ERROR_LIBRO_NO_DISPONIBLE)
            }
            libro is LibroFisico && libro.esReferencia -> {
                throw ValidacionException(Constants.Mensajes.ERROR_LIBRO_REFERENCIA)
            }
            libro is LibroFisico && libro.getEjemplaresDisponibles() <= 0 -> {
                throw ValidacionException("No hay ejemplares disponibles del libro '${libro.titulo}'")
            }
        }
        return true
    }
    
    /**
     * Valida si un usuario puede realizar préstamos.
     */
    fun validarPrestamoUsuario(usuario: Usuario): Boolean {
        if (!usuario.puedeRealizarPrestamos()) {
            throw ValidacionException(Constants.Mensajes.ERROR_USUARIO_CON_ATRASOS)
        }
        return true
    }
    
    /**
     * Valida las credenciales de login de forma segura.
     */
    fun validarCredenciales(email: String, contrasena: String): CredencialesValidadas {
        val emailValido = validarEmail(email)
        val contrasenaValida = validarTextoNoVacio(contrasena, "Contraseña")
        
        return CredencialesValidadas(emailValido, contrasenaValida)
    }
    
    /**
     * Valida una entrada numérica desde string.
     */
    fun validarEnteroDesdeString(entrada: String, campo: String): Int {
        return try {
            entrada.toInt()
        } catch (e: NumberFormatException) {
            throw ValidacionException("El campo '$campo' debe ser un número válido. Entrada: '$entrada'")
        }
    }
    
    /**
     * Valida un ID (debe ser positivo).
     */
    fun validarId(id: Int, entidad: String): Int {
        if (id <= 0) {
            throw ValidacionException("El ID de $entidad debe ser positivo. Actual: $id")
        }
        return id
    }
}

// === DATA CLASSES PARA DATOS VALIDADOS ===

data class LibroValidado(
    val titulo: String,
    val autor: String,
    val categoria: String,
    val anioPublicacion: Int,
    val precioBase: Int,
    val diasPrestamo: Int
)

data class UsuarioValidado(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val tipoUsuario: TipoUsuario
)

data class CredencialesValidadas(
    val email: String,
    val contrasena: String
)