package cl.duoc.dsy1105.booksmart.model

/**
 * Representa un libro digital en la biblioteca.
 * Los libros con DRM tienen restricciones especiales de disponibilidad.
 */
class LibroDigital(
	id: Int,
	titulo: String,
	autor: String,
	categoria: String,
	anioPublicacion: Int,
	precioBase: Int,
	diasPrestamo: Int,
	val drm: Boolean = false,
	val formatoArchivo: String = "PDF",
	val tamanoArchivoMB: Double = 0.0
) : Libro(
	id = id,
	titulo = titulo,
	autor = autor,
	categoria = categoria,
	anioPublicacion = anioPublicacion,
	precioBase = precioBase,
	diasPrestamo = diasPrestamo
) {
	
	init {
		require(tamanoArchivoMB >= 0) { "El tamaño del archivo no puede ser negativo" }
		require(formatoArchivo.isNotBlank()) { "El formato del archivo no puede estar vacío" }
	}
	
	/**
	 * Los libros digitales mantienen su precio base, pero podrían tener
	 * modificaciones en el futuro según restricciones DRM.
	 */
	override fun costoFinal(): Int = precioBase
	
	/**
	 * Un libro digital está disponible si tiene días de préstamo configurados.
	 * Los libros con DRM siempre están disponibles pero con restricciones especiales.
	 */
	override fun estaDisponible(): Boolean {
		return diasPrestamo > 0
	}
	
	/**
	 * Verifica si el libro requiere conexión especial por DRM.
	 */
	fun requiereAutenticacionDRM(): Boolean = drm
	
	/**
	 * Genera la URL de descarga temporal (simulada).
	 */
	fun generarUrlDescarga(): String {
		return "https://booksmart.duoc.cl/downloads/${id}/${titulo.replace(" ", "_").lowercase()}"
	}
	
	override fun descripcion(): String {
		val baseDesc = super.descripcion()
		val drmText = if (drm) " (DRM)" else ""
		val sizeText = if (tamanoArchivoMB > 0) " - ${tamanoArchivoMB}MB" else ""
		return "$baseDesc$drmText$sizeText"
	}
	
	override fun toString(): String {
		return "LibroDigital(id=$id, titulo='$titulo', autor='$autor', " +
				"formato='$formatoArchivo', DRM=$drm, tamaño=${tamanoArchivoMB}MB)"
	}
}