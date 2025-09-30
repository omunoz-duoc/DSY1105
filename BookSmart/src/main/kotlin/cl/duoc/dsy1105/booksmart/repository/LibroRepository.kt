package cl.duoc.dsy1105.booksmart.repository

import cl.duoc.dsy1105.booksmart.model.*

/**
 * Repositorio para gestionar el catálogo de libros del sistema BookSmart.
 * Implementa operaciones CRUD y funciones de búsqueda usando operaciones funcionales.
 */
class LibroRepository {
	private val libros: MutableList<Libro> = mutableListOf()
	private val usuarios: MutableList<Usuario> = mutableListOf()
	private val prestamos: MutableList<Prestamo> = mutableListOf()
	
	init {
		inicializarCatalogo()
		inicializarUsuarios()
	}
	
	/**
	 * Inicializa el catálogo con los datos de prueba sugeridos en los requisitos.
	 */
	private fun inicializarCatalogo() {
		// Libros físicos
		agregarLibro(LibroFisico(
			id = 1,
			titulo = "Estructuras de Datos",
			autor = "Goodrich",
			categoria = "Programacion",
			anioPublicacion = 2020,
			precioBase = 12990,
			diasPrestamo = 7,
			cantidadEjemplares = 3,
			esReferencia = false
		))
		
		agregarLibro(LibroFisico(
			id = 2,
			titulo = "Diccionario Enciclopedico",
			autor = "Varios",
			categoria = "Referencia",
			anioPublicacion = 2019,
			precioBase = 15990,
			diasPrestamo = 0,
			cantidadEjemplares = 1,
			esReferencia = true
		))
		
		// Libros digitales
		agregarLibro(LibroDigital(
			id = 3,
			titulo = "Programacion en Kotlin",
			autor = "JetBrains",
			categoria = "Programación",
			anioPublicacion = 2023,
			precioBase = 9990,
			diasPrestamo = 10,
			drm = true,
			formatoArchivo = "PDF",
			tamanoArchivoMB = 15.5
		))
		
		agregarLibro(LibroDigital(
			id = 4,
			titulo = "Algoritmos Basicos",
			autor = "Cormen",
			categoria = "Algoritmos",
			anioPublicacion = 2022,
			precioBase = 11990,
			diasPrestamo = 10,
			drm = false,
			formatoArchivo = "EPUB",
			tamanoArchivoMB = 8.2
		))
		
		// Libros adicionales para mayor variedad
		agregarLibro(LibroFisico(
			id = 5,
			titulo = "Introduccion a Android",
			autor = "Google Developers",
			categoria = "Desarrollo Movil",
			anioPublicacion = 2024,
			precioBase = 18500,
			diasPrestamo = 14,
			cantidadEjemplares = 2,
			esReferencia = false
		))
	}
	
	/**
	 * Inicializa usuarios de prueba incluyendo el administrador.
	 */
	private fun inicializarUsuarios() {
		// Usuario administrador
		usuarios.add(Usuario(
			id = 1,
			nombre = "Administrador",
			email = "admin@booksmart.com",
			contrasena = "booksmart"
		))
		
		// Usuario estudiante
		usuarios.add(Usuario(
			id = 2,
			nombre = "Oscar Munoz",
			email = "osca.munozs@duocuc.cl",
			contrasena = "123456"
		))
		
		// Usuario docente  
		usuarios.add(Usuario(
			id = 3,
			nombre = "Juan Azocar",
			email = "juan.azocar@duoc.cl",
			contrasena = "docente123"
		))
		
		// Usuario externo
		usuarios.add(Usuario(
			id = 4,
			nombre = "Natalia Silva",
			email = "natalia.silva@gmail.com",
			contrasena = "externo123"
		))
	}
	
	// === OPERACIONES CRUD PARA LIBROS ===
	
	fun agregarLibro(libro: Libro): Boolean {
		return if (!existeLibro(libro.id)) {
			libros.add(libro)
			true
		} else {
			false
		}
	}
	
	fun obtenerLibroPorId(id: Int): Libro? {
		return libros.find { it.id == id }
	}
	
	fun obtenerTodosLosLibros(): List<Libro> = libros.toList()
	
	fun eliminarLibro(id: Int): Boolean {
		return libros.removeIf { it.id == id }
	}
	
	private fun existeLibro(id: Int): Boolean {
		return libros.any { it.id == id }
	}
	
	// === OPERACIONES DE BÚSQUEDA Y FILTRADO (FUNCIONES DE ORDEN SUPERIOR) ===
	
	/**
	 * Busca libros por título usando operaciones funcionales.
	 */
	fun buscarPorTitulo(titulo: String): List<Libro> {
		return libros.filter { 
			it.titulo.contains(titulo, ignoreCase = true) 
		}
	}
	
	/**
	 * Busca libros por autor usando filter.
	 */
	fun buscarPorAutor(autor: String): List<Libro> {
		return libros.filter { 
			it.autor.contains(autor, ignoreCase = true) 
		}
	}
	
	/**
	 * Filtra libros por categoría.
	 */
	fun filtrarPorCategoria(categoria: String): List<Libro> {
		return libros.filter { 
			it.categoria.equals(categoria, ignoreCase = true) 
		}
	}
	
	/**
	 * Obtiene solo los libros disponibles para préstamo.
	 */
	fun obtenerLibrosDisponibles(): List<Libro> {
		return libros.filter { it.estaDisponible() }
	}
	
	/**
	 * Obtiene libros físicos con ejemplares disponibles usando filter y cast.
	 */
	fun obtenerLibrosFisicosDisponibles(): List<LibroFisico> {
		return libros.filterIsInstance<LibroFisico>()
			.filter { it.estaDisponible() && it.getEjemplaresDisponibles() > 0 }
	}
	
	/**
	 * Obtiene libros digitales usando filter.
	 */
	fun obtenerLibrosDigitales(): List<LibroDigital> {
		return libros.filterIsInstance<LibroDigital>()
	}
	
	/**
	 * Calcula estadísticas del catálogo usando sumOf y count.
	 */
	fun obtenerEstadisticasCatalogo(): EstadisticasCatalogo {
		val totalLibros = libros.size
		val librosDisponibles = libros.count { it.estaDisponible() }
		val valorTotalCatalogo = libros.sumOf { it.precioBase }
		val librosFisicos = libros.count { it is LibroFisico }
		val librosDigitales = libros.count { it is LibroDigital }
		val librosReferencia = libros.filterIsInstance<LibroFisico>()
			.count { it.esReferencia }
		
		return EstadisticasCatalogo(
			totalLibros = totalLibros,
			librosDisponibles = librosDisponibles,
			valorTotalCatalogo = valorTotalCatalogo,
			librosFisicos = librosFisicos,
			librosDigitales = librosDigitales,
			librosReferencia = librosReferencia
		)
	}
	
	// === OPERACIONES PARA USUARIOS ===
	
	fun agregarUsuario(usuario: Usuario): Boolean {
		return if (!existeUsuario(usuario.email)) {
			usuarios.add(usuario)
			true
		} else {
			false
		}
	}
	
	fun buscarUsuarioPorEmail(email: String): Usuario? {
		return usuarios.find { it.email == email }
	}
	
	fun autenticarUsuario(email: String, contrasena: String): Usuario? {
		return usuarios.find { it.verificarCredenciales(email, contrasena) }
	}
	
	private fun existeUsuario(email: String): Boolean {
		return usuarios.any { it.email == email }
	}
	
	fun obtenerTodosLosUsuarios(): List<Usuario> = usuarios.toList()
	
	// === OPERACIONES PARA PRÉSTAMOS ===
	
	fun agregarPrestamo(prestamo: Prestamo): Boolean {
		return prestamos.add(prestamo)
	}
	
	fun obtenerPrestamosPorUsuario(usuarioId: Int): List<Prestamo> {
		return prestamos.filter { it.usuario.id == usuarioId }
	}
	
	fun obtenerPrestamosActivos(): List<Prestamo> {
		return prestamos.filter { it.estado.esActivo() }
	}
	
	fun obtenerTodosLosPrestamos(): List<Prestamo> = prestamos.toList()
	
	/**
	 * Calcula estadísticas de préstamos usando operaciones funcionales.
	 */
	fun obtenerEstadisticasPrestamos(): EstadisticasPrestamos {
		val totalPrestamos = prestamos.size
		val prestamosActivos = prestamos.count { it.estado.esActivo() }
		val prestamosAtrasados = prestamos.count { it.estaAtrasado() }
		val montoTotalPrestamos = prestamos.sumOf { it.costoTotal }
		val montoTotalMultas = prestamos.sumOf { it.calcularMulta() }
		
		return EstadisticasPrestamos(
			totalPrestamos = totalPrestamos,
			prestamosActivos = prestamosActivos,
			prestamosAtrasados = prestamosAtrasados,
			montoTotalPrestamos = montoTotalPrestamos,
			montoTotalMultas = montoTotalMultas
		)
	}
}

/**
 * Data class para estadísticas del catálogo.
 */
data class EstadisticasCatalogo(
	val totalLibros: Int,
	val librosDisponibles: Int,
	val valorTotalCatalogo: Int,
	val librosFisicos: Int,
	val librosDigitales: Int,
	val librosReferencia: Int
)

/**
 * Data class para estadísticas de préstamos.
 */
data class EstadisticasPrestamos(
	val totalPrestamos: Int,
	val prestamosActivos: Int,
	val prestamosAtrasados: Int,
	val montoTotalPrestamos: Int,
	val montoTotalMultas: Int
)