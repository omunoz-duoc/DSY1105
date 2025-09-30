package cl.duoc.dsy1105.booksmart.service

import cl.duoc.dsy1105.booksmart.model.Prestamo
import cl.duoc.dsy1105.booksmart.model.Usuario
import cl.duoc.dsy1105.booksmart.util.Constants
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Servicio para calcular multas por atrasos en la devolución de libros.
 * Implementa la lógica de negocio para penalizaciones por retraso.
 */
object CalculadoraMultas {
    
    /**
     * Calcula la multa para un préstamo específico.
     */
    fun calcularMulta(prestamo: Prestamo): MultaCalculada {
        val diasAtraso = calcularDiasAtraso(prestamo.fechaLimiteDevolucion)
        val montoMulta = if (diasAtraso > 0) diasAtraso * Constants.MULTA_POR_DIA_ATRASO else 0
        val costoTotalConMulta = prestamo.costoTotal + montoMulta
        
        return MultaCalculada(
            prestamo = prestamo,
            diasAtraso = diasAtraso,
            montoMulta = montoMulta,
            costoOriginal = prestamo.costoTotal,
            costoTotalConMulta = costoTotalConMulta,
            estaAtrasado = diasAtraso > 0
        )
    }
    
    /**
     * Calcula las multas para múltiples préstamos de un usuario.
     */
    fun calcularMultasUsuario(prestamos: List<Prestamo>): MultasUsuarioCalculadas {
        val multasIndividuales = prestamos.map { calcularMulta(it) }
        val prestamosAtrasados = multasIndividuales.filter { it.estaAtrasado }
        val totalMultas = multasIndividuales.sumOf { it.montoMulta }
        val totalDiasAtraso = prestamosAtrasados.sumOf { it.diasAtraso }
        val costeoOriginal = prestamos.sumOf { it.costoTotal }
        val costoTotalConMultas = costeoOriginal + totalMultas
        
        return MultasUsuarioCalculadas(
            multasIndividuales = multasIndividuales,
            prestamosAtrasados = prestamosAtrasados,
            totalMultas = totalMultas,
            totalDiasAtraso = totalDiasAtraso,
            costoOriginal = costeoOriginal,
            costoTotalConMultas = costoTotalConMultas,
            cantidadPrestamosAtrasados = prestamosAtrasados.size
        )
    }
    
    /**
     * Calcula los días de atraso desde una fecha límite hasta hoy.
     */
    fun calcularDiasAtraso(fechaLimite: LocalDate): Int {
        val hoy = LocalDate.now()
        val diasAtraso = ChronoUnit.DAYS.between(fechaLimite, hoy).toInt()
        return maxOf(0, diasAtraso)
    }
    
    /**
     * Simula el cálculo de multa para una fecha de devolución específica.
     */
    fun simularMulta(fechaLimite: LocalDate, fechaDevolucion: LocalDate): SimulacionMulta {
        val diasAtraso = ChronoUnit.DAYS.between(fechaLimite, fechaDevolucion).toInt()
        val diasAtrasoPositivos = maxOf(0, diasAtraso)
        val montoMulta = diasAtrasoPositivos * Constants.MULTA_POR_DIA_ATRASO
        
        return SimulacionMulta(
            fechaLimite = fechaLimite,
            fechaDevolucion = fechaDevolucion,
            diasAtraso = diasAtrasoPositivos,
            montoMulta = montoMulta,
            estaAtrasado = diasAtraso > 0
        )
    }
    
    /**
     * Verifica si un usuario tiene restricciones por multas acumuladas.
     */
    fun verificarRestriccionesPorMultas(usuario: Usuario, prestamos: List<Prestamo>): RestriccionesMulta {
        val multasCalculadas = calcularMultasUsuario(prestamos)
        val tieneRestricciones = multasCalculadas.cantidadPrestamosAtrasados >= Constants.MAX_ATRASOS_PERMITIDOS
        
        return RestriccionesMulta(
            usuario = usuario,
            tieneRestricciones = tieneRestricciones,
            multasAcumuladas = multasCalculadas,
            motivo = if (tieneRestricciones) {
                "Usuario con ${multasCalculadas.cantidadPrestamosAtrasados} préstamos atrasados. " +
                "Máximo permitido: ${Constants.MAX_ATRASOS_PERMITIDOS}"
            } else {
                "Usuario sin restricciones"
            }
        )
    }
    
    /**
     * Calcula el ranking de usuarios con más multas.
     */
    fun calcularRankingMultas(usuariosConPrestamos: Map<Usuario, List<Prestamo>>): List<RankingMultaUsuario> {
        return usuariosConPrestamos.map { (usuario, prestamos) ->
            val multasCalculadas = calcularMultasUsuario(prestamos)
            RankingMultaUsuario(
                usuario = usuario,
                totalMultas = multasCalculadas.totalMultas,
                cantidadAtrasados = multasCalculadas.cantidadPrestamosAtrasados,
                totalDiasAtraso = multasCalculadas.totalDiasAtraso
            )
        }.sortedByDescending { it.totalMultas }
    }
    
    /**
     * Genera estadísticas generales de multas del sistema.
     */
    fun generarEstadisticasMultas(todosLosPrestamos: List<Prestamo>): EstadisticasMultas {
        val multasIndividuales = todosLosPrestamos.map { calcularMulta(it) }
        val prestamosAtrasados = multasIndividuales.filter { it.estaAtrasado }
        
        val totalMultas = multasIndividuales.sumOf { it.montoMulta }
        val promedioMultaPorPrestamo = if (todosLosPrestamos.isNotEmpty()) {
            totalMultas / todosLosPrestamos.size
        } else 0
        
        val promedioMultaPorAtrasado = if (prestamosAtrasados.isNotEmpty()) {
            prestamosAtrasados.sumOf { it.montoMulta } / prestamosAtrasados.size
        } else 0
        
        return EstadisticasMultas(
            totalPrestamos = todosLosPrestamos.size,
            prestamosAtrasados = prestamosAtrasados.size,
            totalMultas = totalMultas,
            promedioMultaPorPrestamo = promedioMultaPorPrestamo,
            promedioMultaPorAtrasado = promedioMultaPorAtrasado,
            porcentajeAtrasos = if (todosLosPrestamos.isNotEmpty()) {
                (prestamosAtrasados.size.toDouble() / todosLosPrestamos.size) * 100
            } else 0.0
        )
    }
}

// === DATA CLASSES PARA RESULTADOS DE CÁLCULOS ===

/**
 * Resultado del cálculo de multa para un préstamo individual.
 */
data class MultaCalculada(
    val prestamo: Prestamo,
    val diasAtraso: Int,
    val montoMulta: Int,
    val costoOriginal: Int,
    val costoTotalConMulta: Int,
    val estaAtrasado: Boolean
)

/**
 * Resultado del cálculo de multas para todos los préstamos de un usuario.
 */
data class MultasUsuarioCalculadas(
    val multasIndividuales: List<MultaCalculada>,
    val prestamosAtrasados: List<MultaCalculada>,
    val totalMultas: Int,
    val totalDiasAtraso: Int,
    val costoOriginal: Int,
    val costoTotalConMultas: Int,
    val cantidadPrestamosAtrasados: Int
)

/**
 * Simulación de multa para fechas específicas.
 */
data class SimulacionMulta(
    val fechaLimite: LocalDate,
    val fechaDevolucion: LocalDate,
    val diasAtraso: Int,
    val montoMulta: Int,
    val estaAtrasado: Boolean
)

/**
 * Información sobre restricciones por multas acumuladas.
 */
data class RestriccionesMulta(
    val usuario: Usuario,
    val tieneRestricciones: Boolean,
    val multasAcumuladas: MultasUsuarioCalculadas,
    val motivo: String
)

/**
 * Ranking de usuario por multas.
 */
data class RankingMultaUsuario(
    val usuario: Usuario,
    val totalMultas: Int,
    val cantidadAtrasados: Int,
    val totalDiasAtraso: Int
)

/**
 * Estadísticas generales de multas del sistema.
 */
data class EstadisticasMultas(
    val totalPrestamos: Int,
    val prestamosAtrasados: Int,
    val totalMultas: Int,
    val promedioMultaPorPrestamo: Int,
    val promedioMultaPorAtrasado: Int,
    val porcentajeAtrasos: Double
)