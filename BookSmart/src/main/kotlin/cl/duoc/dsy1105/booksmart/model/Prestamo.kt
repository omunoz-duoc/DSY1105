package cl.duoc.dsy1105.booksmart.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Representa un préstamo de un libro a un usuario en el sistema BookSmart.
 */
data class Prestamo(
	val id: Int,
	val libro: Libro,
	val usuario: Usuario,
	val fechaPrestamo: LocalDate = LocalDate.now(),
	val fechaLimiteDevolucion: LocalDate = fechaPrestamo.plusDays(libro.diasPrestamo.toLong()),
	val precioBase: Int = libro.precioBase,
	val descuentoAplicado: Int = usuario.calcularDescuento(libro.precioBase),
	val costoTotal: Int = usuario.aplicarDescuento(libro.precioBase),
	var estado: EstadoPrestamo = EstadoPrestamo.Pendiente,
	var fechaDevolucionReal: LocalDate? = null
) {
	
	init {
		require(precioBase >= 0) { "El precio base no puede ser negativo" }
		require(libro.estaDisponible()) { "No se puede crear un préstamo para un libro no disponible" }
		require(usuario.puedeRealizarPrestamos()) { "El usuario no puede realizar préstamos debido a atrasos" }
	}
	
	/**
	 * Calcula los días restantes para la devolución.
	 * Retorna número negativo si está atrasado.
	 */
	fun diasRestantes(): Int {
		val hoy = LocalDate.now()
		return ChronoUnit.DAYS.between(hoy, fechaLimiteDevolucion).toInt()
	}
	
	/**
	 * Verifica si el préstamo está atrasado.
	 */
	fun estaAtrasado(): Boolean = diasRestantes() < 0
	
	/**
	 * Calcula la multa por atraso si corresponde.
	 */
	fun calcularMulta(): Int {
		if (!estaAtrasado()) return 0
		
		val diasAtraso = Math.abs(diasRestantes())
		// Multa: $100 por día de atraso
		return diasAtraso * 100
	}
	
	/**
	 * Calcula el costo total incluyendo multas si hay atraso.
	 */
	fun costoTotalConMulta(): Int {
		return costoTotal + calcularMulta()
	}
	
	/**
	 * Procesa la devolución del libro.
	 */
	fun procesar() {
		estado = EstadoPrestamo.EnPrestamo(diasRestantes())
	}
	
	/**
	 * Marca el préstamo como devuelto.
	 */
	fun devolver(): Int {
		fechaDevolucionReal = LocalDate.now()
		estado = EstadoPrestamo.Devuelto
		
		// Si es un libro físico, devolver el ejemplar
		if (libro is LibroFisico) {
			libro.devolverEjemplar()
		}
		
		// Registrar atraso si corresponde
		if (estaAtrasado()) {
			usuario.registrarAtraso()
		}
		
		return costoTotalConMulta()
	}
	
	/**
	 * Marca el préstamo como error.
	 */
	fun marcarError(mensaje: String) {
		estado = EstadoPrestamo.Error(mensaje)
	}
	
	/**
	 * Formatea las fechas para mostrar.
	 */
	private fun formatearFecha(fecha: LocalDate): String {
		return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
	}
	
	fun resumenPrestamo(): String {
		val multa = if (estaAtrasado()) " - Multa: $${calcularMulta()}" else ""
		return "${libro.titulo} - ${usuario.nombre} - Vence: ${formatearFecha(fechaLimiteDevolucion)}$multa"
	}
	
	override fun toString(): String {
		return "Prestamo(id=$id, libro='${libro.titulo}', usuario='${usuario.nombre}', " +
				"estado=${estado.descripcion()}, costo=$costoTotal)"
	}
}