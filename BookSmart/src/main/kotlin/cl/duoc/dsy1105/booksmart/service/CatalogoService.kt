package cl.duoc.dsy1105.booksmart.service

import cl.duoc.dsy1105.booksmart.model.*
import cl.duoc.dsy1105.booksmart.repository.LibroRepository
import cl.duoc.dsy1105.booksmart.repository.EstadisticasCatalogo
import cl.duoc.dsy1105.booksmart.util.ValidationUtils
import cl.duoc.dsy1105.booksmart.exception.ValidacionException

/**
 * Servicio para gestión del catálogo de libros.
 * Proporciona operaciones de alto nivel para el manejo del inventario.
 */
class CatalogoService(val repository: LibroRepository) {
    
    /**
     * Obtiene todo el catálogo de libros.
     */
    fun obtenerCatalogo(): List<Libro> {
        return repository.obtenerTodosLosLibros()
    }
    
    /**
     * Obtiene solo los libros disponibles para préstamo.
     */
    fun obtenerLibrosDisponibles(): List<Libro> {
        return repository.obtenerLibrosDisponibles()
    }
    
    /**
     * Busca libros por título usando operaciones funcionales.
     */
    fun buscarLibrosPorTitulo(titulo: String): List<Libro> {
        val tituloValidado = ValidationUtils.validarTextoNoVacio(titulo, "Título de búsqueda")
        return repository.buscarPorTitulo(tituloValidado)
    }
    
    /**
     * Busca libros por autor.
     */
    fun buscarLibrosPorAutor(autor: String): List<Libro> {
        val autorValidado = ValidationUtils.validarTextoNoVacio(autor, "Autor de búsqueda")
        return repository.buscarPorAutor(autorValidado)
    }
    
    /**
     * Filtra libros por categoría.
     */
    fun filtrarPorCategoria(categoria: String): List<Libro> {
        val categoriaValidada = ValidationUtils.validarTextoNoVacio(categoria, "Categoría")
        return repository.filtrarPorCategoria(categoriaValidada)
    }
    
    /**
     * Obtiene un libro específico por ID.
     */
    fun obtenerLibro(id: Int): Libro? {
        ValidationUtils.validarId(id, "libro")
        return repository.obtenerLibroPorId(id)
    }
    
    /**
     * Obtiene categorías únicas del catálogo usando operaciones funcionales.
     */
    fun obtenerCategorias(): List<String> {
        return repository.obtenerTodosLosLibros()
            .map { it.categoria }
            .distinct()
            .sorted()
    }
    
    /**
     * Obtiene autores únicos del catálogo.
     */
    fun obtenerAutores(): List<String> {
        return repository.obtenerTodosLosLibros()
            .map { it.autor }
            .distinct()
            .sorted()
    }
    
    /**
     * Obtiene libros físicos con información de disponibilidad.
     */
    fun obtenerLibrosFisicos(): List<LibroFisicoConDisponibilidad> {
        return repository.obtenerTodosLosLibros()
            .filterIsInstance<LibroFisico>()
            .map { libro ->
                LibroFisicoConDisponibilidad(
                    libro = libro,
                    disponible = libro.estaDisponible(),
                    ejemplaresDisponibles = libro.getEjemplaresDisponibles(),
                    puedePrestar = !libro.esReferencia && libro.getEjemplaresDisponibles() > 0
                )
            }
    }
    
    /**
     * Obtiene libros digitales con información DRM.
     */
    fun obtenerLibrosDigitales(): List<LibroDigitalConInfo> {
        return repository.obtenerTodosLosLibros()
            .filterIsInstance<LibroDigital>()
            .map { libro ->
                LibroDigitalConInfo(
                    libro = libro,
                    disponible = libro.estaDisponible(),
                    requiereAutenticacion = libro.drm,
                    tamanoMB = libro.tamanoArchivoMB
                )
            }
    }
    
    /**
     * Busca libros por múltiples criterios usando operaciones funcionales.
     */
    fun buscarLibrosAvanzado(
        titulo: String? = null,
        autor: String? = null,
        categoria: String? = null,
        soloDisponibles: Boolean = false
    ): List<Libro> {
        var libros = repository.obtenerTodosLosLibros()
        
        // Aplicar filtros usando operaciones funcionales
        titulo?.let { t ->
            val tituloValidado = ValidationUtils.validarTextoNoVacio(t, "Título")
            libros = libros.filter { it.titulo.contains(tituloValidado, ignoreCase = true) }
        }
        
        autor?.let { a ->
            val autorValidado = ValidationUtils.validarTextoNoVacio(a, "Autor")
            libros = libros.filter { it.autor.contains(autorValidado, ignoreCase = true) }
        }
        
        categoria?.let { c ->
            val categoriaValidada = ValidationUtils.validarTextoNoVacio(c, "Categoría")
            libros = libros.filter { it.categoria.equals(categoriaValidada, ignoreCase = true) }
        }
        
        if (soloDisponibles) {
            libros = libros.filter { it.estaDisponible() }
        }
        
        return libros
    }
    
    /**
     * Obtiene estadísticas del catálogo.
     */
    fun obtenerEstadisticas(): EstadisticasCatalogo {
        return repository.obtenerEstadisticasCatalogo()
    }
    
    /**
     * Obtiene libros ordenados por diferentes criterios.
     */
    fun obtenerLibrosOrdenados(criterio: CriterioOrden): List<Libro> {
        val libros = repository.obtenerTodosLosLibros()
        
        return when (criterio) {
            CriterioOrden.TITULO -> libros.sortedBy { it.titulo }
            CriterioOrden.AUTOR -> libros.sortedBy { it.autor }
            CriterioOrden.CATEGORIA -> libros.sortedBy { it.categoria }
            CriterioOrden.ANIO -> libros.sortedBy { it.anioPublicacion }
            CriterioOrden.PRECIO_MENOR -> libros.sortedBy { it.precioBase }
            CriterioOrden.PRECIO_MAYOR -> libros.sortedByDescending { it.precioBase }
            CriterioOrden.DISPONIBILIDAD -> libros.sortedByDescending { it.estaDisponible() }
        }
    }
    
    /**
     * Verifica la disponibilidad de un libro para préstamo.
     */
    fun verificarDisponibilidad(libroId: Int): DisponibilidadLibro {
        val libro = obtenerLibro(libroId)
            ?: return DisponibilidadLibro(
                disponible = false,
                motivo = "Libro no encontrado",
                libro = null
            )
        
        return when {
            !libro.estaDisponible() -> DisponibilidadLibro(
                disponible = false,
                motivo = "Libro no disponible para préstamo",
                libro = libro
            )
            libro is LibroFisico && libro.esReferencia -> DisponibilidadLibro(
                disponible = false,
                motivo = "Los libros de referencia no se pueden prestar",
                libro = libro
            )
            libro is LibroFisico && libro.getEjemplaresDisponibles() <= 0 -> DisponibilidadLibro(
                disponible = false,
                motivo = "No hay ejemplares disponibles",
                libro = libro
            )
            else -> DisponibilidadLibro(
                disponible = true,
                motivo = "Libro disponible para préstamo",
                libro = libro
            )
        }
    }
    
    /**
     * Obtiene recomendaciones basadas en un libro (misma categoría o autor).
     */
    fun obtenerRecomendaciones(libroId: Int, limite: Int = 5): List<Libro> {
        val libro = obtenerLibro(libroId) ?: return emptyList()
        
        return repository.obtenerTodosLosLibros()
            .filter { it.id != libroId } // Excluir el libro actual
            .filter { it.categoria == libro.categoria || it.autor == libro.autor }
            .filter { it.estaDisponible() }
            .take(limite)
    }
}

// === DATA CLASSES PARA RESULTADOS ===

data class LibroFisicoConDisponibilidad(
    val libro: LibroFisico,
    val disponible: Boolean,
    val ejemplaresDisponibles: Int,
    val puedePrestar: Boolean
)

data class LibroDigitalConInfo(
    val libro: LibroDigital,
    val disponible: Boolean,
    val requiereAutenticacion: Boolean,
    val tamanoMB: Double
)

data class DisponibilidadLibro(
    val disponible: Boolean,
    val motivo: String,
    val libro: Libro?
)


enum class CriterioOrden {
    TITULO,
    AUTOR, 
    CATEGORIA,
    ANIO,
    PRECIO_MENOR,
    PRECIO_MAYOR,
    DISPONIBILIDAD
}