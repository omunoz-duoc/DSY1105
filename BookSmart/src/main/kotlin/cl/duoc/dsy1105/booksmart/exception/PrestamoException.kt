package cl.duoc.dsy1105.booksmart.exception

import cl.duoc.dsy1105.booksmart.model.*
import cl.duoc.dsy1105.booksmart.util.Constants

/**
 * Excepción personalizada para errores relacionados con operaciones de préstamo.
 * Se lanza cuando no se puede realizar, procesar o devolver un préstamo.
 */
class PrestamoException(
    message: String,
    val codigo: String = Constants.ErrorCodes.PRESTAMO_INVALIDO,
    val libroId: Int? = null,
    val usuarioId: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Constructor para errores relacionados con libro no disponible.
     */
    constructor(libro: Libro, motivo: String) : this(
        message = "No se puede prestar el libro '${libro.titulo}': $motivo",
        codigo = Constants.ErrorCodes.RECURSO_NO_DISPONIBLE,
        libroId = libro.id
    )
    
    /**
     * Constructor para errores relacionados con usuario no elegible.
     */
    constructor(usuario: Usuario, motivo: String) : this(
        message = "El usuario '${usuario.nombre}' no puede realizar préstamos: $motivo",
        codigo = Constants.ErrorCodes.ACCESO_DENEGADO,
        usuarioId = usuario.id
    )
    
    /**
     * Constructor para errores de préstamo específico.
     */
    constructor(prestamo: Prestamo, motivo: String) : this(
        message = "Error en préstamo #${prestamo.id}: $motivo",
        libroId = prestamo.libro.id,
        usuarioId = prestamo.usuario.id
    )
    
    override fun toString(): String {
        val libroInfo = libroId?.let { " [Libro ID: $it]" } ?: ""
        val usuarioInfo = usuarioId?.let { " [Usuario ID: $it]" } ?: ""
        return "PrestamoException: $message$libroInfo$usuarioInfo [Código: $codigo]"
    }
    
    companion object {
        /**
         * Factory methods para crear excepciones comunes.
         */
        
        fun libroNoDisponible(libro: Libro): PrestamoException {
            return PrestamoException(libro, "El libro no está disponible para préstamo")
        }
        
        fun libroDeReferencia(libro: LibroFisico): PrestamoException {
            return PrestamoException(libro, "Los libros de referencia no se pueden prestar")
        }
        
        fun sinEjemplaresDisponibles(libro: LibroFisico): PrestamoException {
            return PrestamoException(libro, "No hay ejemplares disponibles")
        }
        
        fun usuarioConAtrasos(usuario: Usuario): PrestamoException {
            return PrestamoException(
                usuario, 
                "Tiene ${usuario.getCantidadAtrasos()} atrasos. Máximo permitido: ${Constants.MAX_ATRASOS_PERMITIDOS}"
            )
        }
        
        fun prestamoNoEncontrado(prestamoId: Int): PrestamoException {
            return PrestamoException(
                message = "No se encontró el préstamo con ID: $prestamoId",
                codigo = Constants.ErrorCodes.PRESTAMO_INVALIDO
            )
        }
        
        fun prestamoYaDevuelto(prestamo: Prestamo): PrestamoException {
            return PrestamoException(prestamo, "El préstamo ya fue devuelto anteriormente")
        }
        
        fun errorProcesamiento(motivo: String): PrestamoException {
            return PrestamoException(
                message = "Error durante el procesamiento del préstamo: $motivo",
                codigo = Constants.ErrorCodes.PRESTAMO_INVALIDO
            )
        }
    }
}