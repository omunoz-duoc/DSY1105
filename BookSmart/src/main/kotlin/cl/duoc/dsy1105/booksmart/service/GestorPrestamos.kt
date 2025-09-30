package cl.duoc.dsy1105.booksmart.service

import cl.duoc.dsy1105.booksmart.model.*
import cl.duoc.dsy1105.booksmart.repository.LibroRepository
import cl.duoc.dsy1105.booksmart.repository.EstadisticasPrestamos
import cl.duoc.dsy1105.booksmart.exception.PrestamoException
import cl.duoc.dsy1105.booksmart.util.ValidationUtils
import java.time.LocalDate

/**
 * Gestor principal para operaciones de préstamos en el sistema BookSmart.
 * Coordina las operaciones entre repositorio, calculadoras y validaciones.
 */
class GestorPrestamos(
    private val repository: LibroRepository,
    private val calculadoraDescuentos: CalculadoraDescuentos = CalculadoraDescuentos,
    private val calculadoraMultas: CalculadoraMultas = CalculadoraMultas
) {
    
    private var siguienteIdPrestamo = 1
    
    /**
     * Crea un nuevo préstamo validando todas las condiciones de negocio.
     */
    fun crearPrestamo(libroId: Int, usuario: Usuario): ResultadoPrestamo {
        try {
            // Validar datos de entrada
            ValidationUtils.validarId(libroId, "libro")
            ValidationUtils.validarPrestamoUsuario(usuario)
            
            // Obtener el libro
            val libro = repository.obtenerLibroPorId(libroId)
                ?: return ResultadoPrestamo.Error("Libro no encontrado con ID: $libroId")
            
            // Validar disponibilidad del libro
            ValidationUtils.validarPrestamoLibro(libro)
            
            // Crear el préstamo
            val prestamo = Prestamo(
                id = siguienteIdPrestamo++,
                libro = libro,
                usuario = usuario
            )
            
            // Reservar el ejemplar si es libro físico
            if (libro is LibroFisico) {
                if (!libro.prestarEjemplar()) {
                    return ResultadoPrestamo.Error("No se pudo reservar el ejemplar del libro")
                }
            }
            
            // Guardar en repositorio
            repository.agregarPrestamo(prestamo)
            
            return ResultadoPrestamo.Exito(
                prestamo = prestamo,
                mensaje = "Préstamo creado exitosamente"
            )
            
        } catch (e: PrestamoException) {
            return ResultadoPrestamo.Error(e.message ?: "Error en el préstamo", e.codigo)
        } catch (e: Exception) {
            return ResultadoPrestamo.Error("Error inesperado: ${e.message}")
        }
    }
    
    /**
     * Procesa un préstamo (simula el tiempo de procesamiento).
     */
    suspend fun procesarPrestamo(prestamo: Prestamo): ResultadoPrestamo {
        try {
            // Cambiar estado a procesando
            prestamo.estado = EstadoPrestamo.Pendiente
            
            // Procesar el préstamo
            prestamo.procesar()
            
            return ResultadoPrestamo.Exito(
                prestamo = prestamo,
                mensaje = "Préstamo procesado exitosamente"
            )
            
        } catch (e: Exception) {
            prestamo.marcarError("Error durante el procesamiento: ${e.message}")
            return ResultadoPrestamo.Error("Error procesando préstamo: ${e.message}")
        }
    }
    
    /**
     * Procesa la devolución de un libro.
     */
    fun devolverLibro(prestamoId: Int): ResultadoDevolucion {
        try {
            val prestamo = repository.obtenerTodosLosPrestamos()
                .find { it.id == prestamoId }
                ?: return ResultadoDevolucion.Error("Préstamo no encontrado")
            
            if (prestamo.estado is EstadoPrestamo.Devuelto) {
                return ResultadoDevolucion.Error("El libro ya fue devuelto anteriormente")
            }
            
            val costoFinal = prestamo.devolver()
            
            return ResultadoDevolucion.Exito(
                prestamo = prestamo,
                costoFinal = costoFinal,
                multa = prestamo.calcularMulta(),
                mensaje = "Libro devuelto exitosamente"
            )
            
        } catch (e: Exception) {
            return ResultadoDevolucion.Error("Error devolviendo libro: ${e.message}")
        }
    }
    
    /**
     * Obtiene todos los préstamos de un usuario.
     */
    fun obtenerPrestamosUsuario(usuarioId: Int): List<Prestamo> {
        ValidationUtils.validarId(usuarioId, "usuario")
        return repository.obtenerPrestamosPorUsuario(usuarioId)
    }
    
    /**
     * Obtiene los préstamos activos de un usuario.
     */
    fun obtenerPrestamosActivos(usuarioId: Int): List<Prestamo> {
        return obtenerPrestamosUsuario(usuarioId)
            .filter { it.estado.esActivo() }
    }
    
    /**
     * Obtiene los préstamos atrasados de un usuario.
     */
    fun obtenerPrestamosAtrasados(usuarioId: Int): List<Prestamo> {
        return obtenerPrestamosUsuario(usuarioId)
            .filter { it.estaAtrasado() }
    }
    
    /**
     * Calcula el costo total de préstamos para un usuario incluyendo multas.
     */
    fun calcularCostoTotalUsuario(usuarioId: Int): CostoTotalUsuario {
        val prestamos = obtenerPrestamosUsuario(usuarioId)
        val multasCalculadas = calculadoraMultas.calcularMultasUsuario(prestamos)
        
        return CostoTotalUsuario(
            costoPrestamos = prestamos.sumOf { it.costoTotal },
            multas = multasCalculadas.totalMultas,
            costoTotal = multasCalculadas.costoTotalConMultas,
            cantidadPrestamos = prestamos.size,
            cantidadAtrasados = multasCalculadas.cantidadPrestamosAtrasados
        )
    }
    
    /**
     * Genera un reporte de préstamos usando operaciones funcionales.
     */
    fun generarReportePrestamos(): ReportePrestamos {
        val todosPrestamos = repository.obtenerTodosLosPrestamos()
        
        // Usar operaciones funcionales para generar estadísticas
        val totalPrestamos = todosPrestamos.size
        val prestamosActivos = todosPrestamos.count { it.estado.esActivo() }
        val prestamosDevueltos = todosPrestamos.count { it.estado is EstadoPrestamo.Devuelto }
        val prestamosAtrasados = todosPrestamos.count { it.estaAtrasado() }
        
        val ingresosTotales = todosPrestamos.sumOf { it.costoTotal }
        val multasTotales = todosPrestamos.sumOf { it.calcularMulta() }
        
        val librosMasPrestados = todosPrestamos
            .groupBy { it.libro }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
        
        val estadisticasMultas = calculadoraMultas.generarEstadisticasMultas(todosPrestamos)
        
        return ReportePrestamos(
            totalPrestamos = totalPrestamos,
            prestamosActivos = prestamosActivos,
            prestamosDevueltos = prestamosDevueltos,
            prestamosAtrasados = prestamosAtrasados,
            ingresosTotales = ingresosTotales,
            multasTotales = multasTotales,
            librosMasPrestados = librosMasPrestados,
            estadisticasMultas = estadisticasMultas
        )
    }
    
    /**
     * Simula el costo de un préstamo antes de crearlo.
     */
    fun simularCostoPrestamo(libroId: Int, usuario: Usuario): SimulacionCosto? {
        val libro = repository.obtenerLibroPorId(libroId) ?: return null
        
        val descuentoCalculado = calculadoraDescuentos.calcularDescuento(usuario, libro)
        
        return SimulacionCosto(
            libro = libro,
            usuario = usuario,
            precioBase = libro.precioBase,
            descuento = descuentoCalculado.montoDescuento,
            precioFinal = descuentoCalculado.precioFinal,
            diasPrestamo = libro.diasPrestamo
        )
    }
    
    /**
     * Verifica si un usuario puede realizar más préstamos.
     */
    fun puedeRealizarPrestamo(usuario: Usuario): VerificacionPrestamo {
        val prestamosActivos = obtenerPrestamosActivos(usuario.id)
        val prestamosAtrasados = obtenerPrestamosAtrasados(usuario.id)
        
        val restriccionesPorMultas = calculadoraMultas.verificarRestriccionesPorMultas(
            usuario, 
            obtenerPrestamosUsuario(usuario.id)
        )
        
        val puedePrestar = usuario.puedeRealizarPrestamos() && 
                          !restriccionesPorMultas.tieneRestricciones &&
                          prestamosActivos.size < 5 // Límite de préstamos simultáneos
        
        val motivos = mutableListOf<String>()
        if (!usuario.puedeRealizarPrestamos()) {
            motivos.add("Usuario con atrasos registrados")
        }
        if (restriccionesPorMultas.tieneRestricciones) {
            motivos.add("Demasiadas multas acumuladas")
        }
        if (prestamosActivos.size >= 5) {
            motivos.add("Límite de préstamos simultáneos alcanzado")
        }
        
        return VerificacionPrestamo(
            puedePrestar = puedePrestar,
            motivos = motivos,
            prestamosActivos = prestamosActivos.size,
            prestamosAtrasados = prestamosAtrasados.size
        )
    }
    
    /**
     * Obtiene estadísticas de préstamos del repositorio.
     */
    fun obtenerEstadisticasPrestamos(): EstadisticasPrestamos {
        return repository.obtenerEstadisticasPrestamos()
    }
}

// === DATA CLASSES PARA RESULTADOS ===

sealed class ResultadoDevolucion {
    data class Exito(
        val prestamo: Prestamo,
        val costoFinal: Int,
        val multa: Int,
        val mensaje: String
    ) : ResultadoDevolucion()
    
    data class Error(val mensaje: String) : ResultadoDevolucion()
}

data class CostoTotalUsuario(
    val costoPrestamos: Int,
    val multas: Int,
    val costoTotal: Int,
    val cantidadPrestamos: Int,
    val cantidadAtrasados: Int
)

data class ReportePrestamos(
    val totalPrestamos: Int,
    val prestamosActivos: Int,
    val prestamosDevueltos: Int,
    val prestamosAtrasados: Int,
    val ingresosTotales: Int,
    val multasTotales: Int,
    val librosMasPrestados: List<Pair<Libro, Int>>,
    val estadisticasMultas: EstadisticasMultas
)

data class SimulacionCosto(
    val libro: Libro,
    val usuario: Usuario,
    val precioBase: Int,
    val descuento: Int,
    val precioFinal: Int,
    val diasPrestamo: Int
)

data class VerificacionPrestamo(
    val puedePrestar: Boolean,
    val motivos: List<String>,
    val prestamosActivos: Int,
    val prestamosAtrasados: Int
)