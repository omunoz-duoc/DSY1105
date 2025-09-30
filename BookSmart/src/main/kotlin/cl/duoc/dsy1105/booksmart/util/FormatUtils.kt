package cl.duoc.dsy1105.booksmart.util

import cl.duoc.dsy1105.booksmart.model.*
import cl.duoc.dsy1105.booksmart.repository.EstadisticasCatalogo
import cl.duoc.dsy1105.booksmart.repository.EstadisticasPrestamos
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Utilidades para formateo de datos y presentación en el sistema BookSmart.
 */
object FormatUtils {
    
    private val formatoPrecio = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    private val formatoFecha = DateTimeFormatter.ofPattern(Constants.Formato.FORMATO_FECHA)
    
    /**
     * Formatea un precio en pesos chilenos.
     */
    fun formatearPrecio(precio: Int): String {
        return formatoPrecio.format(precio)
    }
    
    /**
     * Formatea un precio con descuento mostrando el precio original tachado.
     */
    fun formatearPrecioConDescuento(precioOriginal: Int, precioConDescuento: Int): String {
        return if (precioOriginal != precioConDescuento) {
            "~~${formatearPrecio(precioOriginal)}~~ **${formatearPrecio(precioConDescuento)}**"
        } else {
            formatearPrecio(precioOriginal)
        }
    }
    
    /**
     * Formatea una fecha LocalDate.
     */
    fun formatearFecha(fecha: LocalDate): String {
        return fecha.format(formatoFecha)
    }
    
    /**
     * Formatea un porcentaje.
     */
    fun formatearPorcentaje(porcentaje: Double): String {
        return "${(porcentaje * 100).toInt()}%"
    }
    
    /**
     * Genera una línea de separación.
     */
    fun separador(longitud: Int = Constants.UI.ANCHO_PANTALLA, caracter: Char = '='): String {
        return caracter.toString().repeat(longitud)
    }
    
    /**
     * Centra un texto en una línea de ancho específico.
     */
    fun centrarTexto(texto: String, ancho: Int = Constants.UI.ANCHO_PANTALLA): String {
        val padding = (ancho - texto.length) / 2
        return " ".repeat(maxOf(0, padding)) + texto
    }
    
    /**
     * Formatea información de un libro para mostrar en catálogo.
     */
    fun formatearLibroParaCatalogo(libro: Libro): String {
        val tipoIndicador = when (libro) {
            is LibroFisico -> Constants.UI.SIMBOLO_FISICO
            is LibroDigital -> Constants.UI.SIMBOLO_DIGITAL
            else -> ""
        }
        
        val disponibilidad = if (libro.estaDisponible()) {
            Constants.UI.SIMBOLO_DISPONIBLE
        } else {
            Constants.UI.SIMBOLO_NO_DISPONIBLE
        }
        
        val infoEspecial = when (libro) {
            is LibroFisico -> {
                val ref = if (libro.esReferencia) " ${Constants.UI.SIMBOLO_REFERENCIA}" else ""
                val ejemplares = if (!libro.esReferencia) " (${libro.getEjemplaresDisponibles()}/${libro.cantidadEjemplares})" else ""
                "$ref$ejemplares"
            }
            is LibroDigital -> {
                if (libro.drm) " ${Constants.UI.SIMBOLO_DRM}" else ""
            }
            else -> ""
        }
        
        return "$tipoIndicador [$disponibilidad] ${libro.titulo} - ${libro.autor} (${libro.anioPublicacion})$infoEspecial"
    }
    
    /**
     * Formatea información detallada de un libro.
     */
    fun formatearDetalleLibro(libro: Libro, usuario: Usuario? = null): String {
        val sb = StringBuilder()
        
        sb.appendLine(separador())
        sb.appendLine(centrarTexto("INFORMACION DEL LIBRO"))
        sb.appendLine(separador())
        sb.appendLine("Titulo: ${libro.titulo}")
        sb.appendLine("Autor: ${libro.autor}")
        sb.appendLine("Categoria: ${libro.categoria}")
        sb.appendLine("Anio: ${libro.anioPublicacion}")
        sb.appendLine("Dias de prestamo: ${libro.diasPrestamo}")
        
        // Precio con descuento si hay usuario
        if (usuario != null) {
            val precioOriginal = libro.precioBase
            val precioConDescuento = usuario.aplicarDescuento(precioOriginal)
            sb.appendLine("Precio: ${formatearPrecioConDescuento(precioOriginal, precioConDescuento)}")
            if (usuario.tipoUsuario.descuento > 0) {
                sb.appendLine("Descuento ${usuario.tipoUsuario.descripcion}: ${formatearPorcentaje(usuario.tipoUsuario.descuento)}")
            }
        } else {
            sb.appendLine("Precio: ${formatearPrecio(libro.precioBase)}")
        }
        
        // Información específica del tipo de libro
        when (libro) {
            is LibroFisico -> {
                sb.appendLine("Tipo: Libro Fisico")
                sb.appendLine("Ejemplares: ${libro.getEjemplaresDisponibles()}/${libro.cantidadEjemplares}")
                if (libro.esReferencia) {
                    sb.appendLine("*** LIBRO DE REFERENCIA - NO SE PUEDE PRESTAR ***")
                }
            }
            is LibroDigital -> {
                sb.appendLine("Tipo: Libro Digital")
                sb.appendLine("Formato: ${libro.formatoArchivo}")
                sb.appendLine("Tamanio: ${libro.tamanoArchivoMB} MB")
                if (libro.drm) {
                    sb.appendLine("DRM: Si (Requiere autenticacion)")
                }
            }
        }
        
        sb.appendLine(separador())
        return sb.toString()
    }
    
    /**
     * Formatea información de un préstamo.
     */
    fun formatearPrestamo(prestamo: Prestamo): String {
        val sb = StringBuilder()
        
        sb.appendLine("ID: ${prestamo.id}")
        sb.appendLine("Libro: ${prestamo.libro.titulo}")
        sb.appendLine("Fecha prestamo: ${formatearFecha(prestamo.fechaPrestamo)}")
        sb.appendLine("Fecha limite: ${formatearFecha(prestamo.fechaLimiteDevolucion)}")
        sb.appendLine("Dias restantes: ${prestamo.diasRestantes()}")
        sb.appendLine("Costo: ${formatearPrecio(prestamo.costoTotal)}")
        
        if (prestamo.estaAtrasado()) {
            sb.appendLine("*** ATRASADO - Multa: ${formatearPrecio(prestamo.calcularMulta())} ***")
            sb.appendLine("Total con multa: ${formatearPrecio(prestamo.costoTotalConMulta())}")
        }
        
        sb.appendLine("Estado: ${prestamo.estado.descripcion()}")
        
        return sb.toString()
    }
    
    /**
     * Formatea un resumen de préstamo para lista.
     */
    fun formatearResumenPrestamo(prestamo: Prestamo): String {
        val estado = if (prestamo.estaAtrasado()) "¡ATRASADO!" else "Activo"
        val multa = if (prestamo.estaAtrasado()) " - Multa: ${formatearPrecio(prestamo.calcularMulta())}" else ""
        
        return "${prestamo.libro.titulo} - Vence: ${formatearFecha(prestamo.fechaLimiteDevolucion)} [$estado]$multa"
    }
    
    /**
     * Formatea estadísticas del catálogo.
     */
    fun formatearEstadisticasCatalogo(estadisticas: EstadisticasCatalogo): String {
        val sb = StringBuilder()
        
        sb.appendLine(separador())
        sb.appendLine(centrarTexto("ESTADÍSTICAS DEL CATÁLOGO"))
        sb.appendLine(separador())
        sb.appendLine("Total de libros: ${estadisticas.totalLibros}")
        sb.appendLine("Libros disponibles: ${estadisticas.librosDisponibles}")
        sb.appendLine("Libros fisicos: ${estadisticas.librosFisicos}")
        sb.appendLine("Libros digitales: ${estadisticas.librosDigitales}")
        sb.appendLine("Libros de referencia: ${estadisticas.librosReferencia}")
        sb.appendLine("Valor total del catálogo: ${formatearPrecio(estadisticas.valorTotalCatalogo)}")
        sb.appendLine(separador())
        
        return sb.toString()
    }
    
    /**
     * Formatea estadísticas de préstamos.
     */
    fun formatearEstadisticasPrestamos(estadisticas: EstadisticasPrestamos): String {
        val sb = StringBuilder()
        
        sb.appendLine(separador())
        sb.appendLine(centrarTexto("ESTADÍSTICAS DE PRÉSTAMOS"))
        sb.appendLine(separador())
        sb.appendLine("Total de préstamos: ${estadisticas.totalPrestamos}")
        sb.appendLine("Prestamos activos: ${estadisticas.prestamosActivos}")
        sb.appendLine("Prestamos atrasados: ${estadisticas.prestamosAtrasados}")
        sb.appendLine("Monto total prestado: ${formatearPrecio(estadisticas.montoTotalPrestamos)}")
        sb.appendLine("Monto total en multas: ${formatearPrecio(estadisticas.montoTotalMultas)}")
        sb.appendLine(separador())
        
        return sb.toString()
    }
    
    /**
     * Crea un encabezado con título y separadores.
     */
    fun crearEncabezado(titulo: String): String {
        val sb = StringBuilder()
        sb.appendLine(separador())
        sb.appendLine(centrarTexto(titulo))
        sb.appendLine(separador())
        return sb.toString()
    }
    
    /**
     * Formatea una lista numerada de opciones.
     */
    fun formatearMenu(opciones: List<String>, titulo: String = "MENÚ"): String {
        val sb = StringBuilder()
        
        sb.appendLine(crearEncabezado(titulo))
        opciones.forEachIndexed { index, opcion ->
            sb.appendLine("${index + 1}. $opcion")
        }
        sb.appendLine("0. Salir")
        sb.appendLine(separador(Constants.UI.ANCHO_PANTALLA / 2, '-'))
        
        return sb.toString()
    }
}