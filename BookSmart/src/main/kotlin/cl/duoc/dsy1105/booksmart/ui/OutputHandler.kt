package cl.duoc.dsy1105.booksmart.ui

import cl.duoc.dsy1105.booksmart.model.EstadoPrestamo
import cl.duoc.dsy1105.booksmart.model.Libro
import cl.duoc.dsy1105.booksmart.model.Prestamo
import cl.duoc.dsy1105.booksmart.model.ResultadoPrestamo
import cl.duoc.dsy1105.booksmart.model.Usuario
import cl.duoc.dsy1105.booksmart.util.FormatUtils

/**
 * Utilidades de salida para la interfaz de consola de BookSmart.
 * Centraliza mensajes y formateos comunes para mantener la consistencia visual.
 */
class OutputHandler {

    fun limpiarPantalla(lineas: Int = 40) {
        repeat(lineas) { println() }
    }

    fun mostrarTitulo(titulo: String) {
        println()
        println(FormatUtils.separador())
        println(FormatUtils.centrarTexto(titulo))
        println(FormatUtils.separador())
    }

    fun mostrarSubtitulo(subtitulo: String) {
        println()
        println(subtitulo)
        println("-".repeat(subtitulo.length.coerceAtLeast(10)))
    }

    fun mostrarInfo(mensaje: String) = println("ℹ️  $mensaje")

    fun mostrarExito(mensaje: String) = println("✅ $mensaje")

    fun mostrarAdvertencia(mensaje: String) = println("⚠️ $mensaje")

    fun mostrarError(mensaje: String) = println("❌ $mensaje")

    fun mostrarCatalogo(libros: List<Libro>) {
        if (libros.isEmpty()) {
            println("📚 No hay libros registrados en el catálogo.")
            return
        }

        mostrarTitulo("CATÁLOGO DE LIBROS")
        libros.forEachIndexed { indice, libro ->
            println("${indice + 1}. ${FormatUtils.formatearLibroParaCatalogo(libro)}")
        }
        println()
    }

    fun mostrarDetalleLibro(libro: Libro, usuario: Usuario? = null) {
        println(FormatUtils.formatearDetalleLibro(libro, usuario))
    }

    fun mostrarPrestamos(prestamos: List<Prestamo>) {
        if (prestamos.isEmpty()) {
            println("📋 No existen préstamos registrados.")
            return
        }

        mostrarTitulo("PRÉSTAMOS ACTIVOS")
        prestamos.forEachIndexed { indice, prestamo ->
            println("${indice + 1}. ${FormatUtils.formatearResumenPrestamo(prestamo)}")
        }
        println()
    }

    fun mostrarPrestamoDetalle(prestamo: Prestamo) {
        println(FormatUtils.formatearPrestamo(prestamo))
    }

    fun mostrarResultadoPrestamo(resultado: ResultadoPrestamo) {
        when (resultado) {
            is ResultadoPrestamo.Exito -> {
                mostrarExito(resultado.mensaje)
                mostrarPrestamoDetalle(resultado.prestamo)
            }
            is ResultadoPrestamo.Error -> mostrarError(resultado.mensaje)
            is ResultadoPrestamo.Procesando -> mostrarInfo("${resultado.mensaje} (${resultado.progreso}%)")
            is ResultadoPrestamo.Validando -> mostrarInfo(resultado.mensaje)
        }
    }

    fun mostrarEstadoPrestamo(prestamo: Prestamo) {
        val estado = when (prestamo.estado) {
            is EstadoPrestamo.Pendiente -> "Pendiente"
            is EstadoPrestamo.EnPrestamo -> "En préstamo"
            is EstadoPrestamo.Devuelto -> "Devuelto"
            is EstadoPrestamo.Error -> "Error"
        }
        println("Estado del préstamo: $estado")
    }

    fun mostrarEstadisticas(titulo: String, datos: Map<String, Any>) {
        mostrarTitulo(titulo)
        datos.forEach { (clave, valor) ->
            println("• $clave: $valor")
        }
        println()
    }
}