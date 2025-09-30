package cl.duoc.dsy1105.booksmart.service

import cl.duoc.dsy1105.booksmart.model.*
import cl.duoc.dsy1105.booksmart.util.Constants

/**
 * Servicio para calcular descuentos según el tipo de usuario.
 * Implementa la lógica de negocio para aplicar descuentos diferenciados.
 */
object CalculadoraDescuentos {
    
    /**
     * Calcula el descuento que corresponde a un usuario para un libro específico.
     */
    fun calcularDescuento(usuario: Usuario, libro: Libro): DescuentoCalculado {
        val precioBase = libro.precioBase
        val porcentajeDescuento = usuario.tipoUsuario.descuento
        val montoDescuento = usuario.calcularDescuento(precioBase)
        val precioFinal = precioBase - montoDescuento
        
        return DescuentoCalculado(
            precioBase = precioBase,
            porcentajeDescuento = porcentajeDescuento,
            montoDescuento = montoDescuento,
            precioFinal = precioFinal,
            tipoUsuario = usuario.tipoUsuario
        )
    }
    
    /**
     * Calcula descuentos para múltiples libros (carrito de préstamos).
     */
    fun calcularDescuentoMultiple(usuario: Usuario, libros: List<Libro>): DescuentoMultipleCalculado {
        val descuentosIndividuales = libros.map { libro ->
            calcularDescuento(usuario, libro)
        }
        
        val subtotal = descuentosIndividuales.sumOf { it.precioBase }
        val totalDescuentos = descuentosIndividuales.sumOf { it.montoDescuento }
        val totalFinal = descuentosIndividuales.sumOf { it.precioFinal }
        
        return DescuentoMultipleCalculado(
            descuentosIndividuales = descuentosIndividuales,
            subtotal = subtotal,
            totalDescuentos = totalDescuentos,
            totalFinal = totalFinal,
            cantidadLibros = libros.size
        )
    }
    
    /**
     * Verifica si un usuario es elegible para descuentos especiales.
     */
    fun esElegibleParaDescuentos(usuario: Usuario): Boolean {
        return usuario.tipoUsuario.descuento > 0.0 && usuario.puedeRealizarPrestamos()
    }
    
    /**
     * Obtiene información sobre el tipo de descuento que aplica al usuario.
     */
    fun obtenerInfoDescuento(tipoUsuario: TipoUsuario): InfoDescuento {
        return when (tipoUsuario) {
            TipoUsuario.ESTUDIANTE -> InfoDescuento(
                descripcion = "Descuento Estudiante DUOC UC",
                porcentaje = Constants.DESCUENTO_ESTUDIANTE,
                condiciones = "Válido para estudiantes con email @duocuc.cl"
            )
            TipoUsuario.DOCENTE -> InfoDescuento(
                descripcion = "Descuento Docente DUOC UC", 
                porcentaje = Constants.DESCUENTO_DOCENTE,
                condiciones = "Válido para docentes con email @duoc.cl"
            )
            TipoUsuario.EXTERNO -> InfoDescuento(
                descripcion = "Sin descuento",
                porcentaje = Constants.DESCUENTO_EXTERNO,
                condiciones = "No aplican descuentos para usuarios externos"
            )
            TipoUsuario.ADMIN -> InfoDescuento(
                descripcion = "Usuario administrador",
                porcentaje = Constants.DESCUENTO_EXTERNO,
                condiciones = "Los administradores no realizan préstamos"
            )
        }
    }
    
    /**
     * Calcula el ahorro total que ha obtenido un usuario por sus descuentos.
     */
    fun calcularAhorroAcumulado(prestamos: List<Prestamo>): AhorroAcumulado {
        val totalSinDescuento = prestamos.sumOf { it.precioBase }
        val totalConDescuento = prestamos.sumOf { it.costoTotal }
        val ahorroTotal = totalSinDescuento - totalConDescuento
        val cantidadPrestamos = prestamos.size
        val ahorroPromedio = if (cantidadPrestamos > 0) ahorroTotal / cantidadPrestamos else 0
        
        return AhorroAcumulado(
            totalSinDescuento = totalSinDescuento,
            totalConDescuento = totalConDescuento,
            ahorroTotal = ahorroTotal,
            cantidadPrestamos = cantidadPrestamos,
            ahorroPromedio = ahorroPromedio
        )
    }
    
    /**
     * Simula el cálculo de descuento sin crear el préstamo.
     */
    fun simularDescuento(tipoUsuario: TipoUsuario, precioBase: Int): SimulacionDescuento {
        val porcentajeDescuento = tipoUsuario.descuento
        val montoDescuento = (precioBase * porcentajeDescuento).toInt()
        val precioFinal = precioBase - montoDescuento
        
        return SimulacionDescuento(
            precioOriginal = precioBase,
            porcentajeDescuento = porcentajeDescuento,
            montoDescuento = montoDescuento,
            precioFinal = precioFinal,
            tipoUsuario = tipoUsuario
        )
    }
}

// === DATA CLASSES PARA RESULTADOS DE CÁLCULOS ===

/**
 * Resultado del cálculo de descuento para un libro individual.
 */
data class DescuentoCalculado(
    val precioBase: Int,
    val porcentajeDescuento: Double,
    val montoDescuento: Int,
    val precioFinal: Int,
    val tipoUsuario: TipoUsuario
)

/**
 * Resultado del cálculo de descuento para múltiples libros.
 */
data class DescuentoMultipleCalculado(
    val descuentosIndividuales: List<DescuentoCalculado>,
    val subtotal: Int,
    val totalDescuentos: Int,
    val totalFinal: Int,
    val cantidadLibros: Int
)

/**
 * Información sobre un tipo de descuento.
 */
data class InfoDescuento(
    val descripcion: String,
    val porcentaje: Double,
    val condiciones: String
)

/**
 * Resultado del cálculo de ahorro acumulado.
 */
data class AhorroAcumulado(
    val totalSinDescuento: Int,
    val totalConDescuento: Int,
    val ahorroTotal: Int,
    val cantidadPrestamos: Int,
    val ahorroPromedio: Int
)

/**
 * Simulación de descuento sin crear préstamo real.
 */
data class SimulacionDescuento(
    val precioOriginal: Int,
    val porcentajeDescuento: Double,
    val montoDescuento: Int,
    val precioFinal: Int,
    val tipoUsuario: TipoUsuario
)