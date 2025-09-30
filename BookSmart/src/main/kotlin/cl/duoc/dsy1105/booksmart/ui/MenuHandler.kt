package cl.duoc.dsy1105.booksmart.ui

import cl.duoc.dsy1105.booksmart.model.Libro
import cl.duoc.dsy1105.booksmart.model.LibroDigital
import cl.duoc.dsy1105.booksmart.model.LibroFisico
import cl.duoc.dsy1105.booksmart.model.Prestamo
import cl.duoc.dsy1105.booksmart.model.ResultadoPrestamo
import cl.duoc.dsy1105.booksmart.model.TipoUsuario
import cl.duoc.dsy1105.booksmart.model.Usuario
import cl.duoc.dsy1105.booksmart.service.CatalogoService
import cl.duoc.dsy1105.booksmart.service.GestorPrestamos
import cl.duoc.dsy1105.booksmart.service.ResultadoDevolucion
import cl.duoc.dsy1105.booksmart.util.FormatUtils
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

/**
 * Controla la interacción por consola del sistema BookSmart.
 * Implementa los flujos de login/registro, panel de administrador y panel de usuario.
 */
class MenuHandler(
	private val catalogoService: CatalogoService,
	private val gestorPrestamos: GestorPrestamos
) {

	private var usuarioActual: Usuario? = null
	private val usuariosRegistrados = mutableMapOf<String, Usuario>()
	private var contadorUsuarios = 1

	/**
	 * Punto de entrada del menú interactivo.
	 */
	suspend fun iniciar() {
		mostrarBienvenida()
		while (true) {
			if (usuarioActual == null) {
				mostrarPantallaLogin()
			} else {
				when (usuarioActual!!.tipoUsuario) {
					TipoUsuario.ADMIN -> mostrarMenuAdministrador()
					else -> mostrarMenuUsuario()
				}
			}
		}
	}

	private fun mostrarBienvenida() {
		println("\n${"=".repeat(60)}")
		println("Bienvenido/a a BookSmart ")
		println("Gestiona tus prestamos de biblioteca de forma sencilla")
		println("(Cuenta de administrador:  admin:booksmart )")
		println("${"=".repeat(60)}")
	}

	/**
	 * Implementa la pantalla principal (login/registro/catálogo invitado).
	 */
	private fun mostrarPantallaLogin() {
		println("\n--- Pantalla de Inicio ---")
		println("1. Iniciar sesion")
		println("2. Registrarse")
		println("3. Ver catalogo (invitado)")
		println("0. Salir")
		print("Seleccione una opcion: ")

		when (readLine()?.trim()) {
			"1" -> procesarInicioSesion()
			"2" -> procesarRegistroUsuario()
			"3" -> mostrarCatalogoComoInvitado()
			"0" -> salirDelSistema()
			else -> println("Opcion no valida. Intente otra vez.")
		}
	}

	private fun procesarInicioSesion() {
		println("\n--- Iniciar sesion ---")
		print("Usuario (email o 'admin'): ")
		val entradaUsuario = readLine()?.trim().orEmpty()

		print("Contrasena: ")
		val contrasena = readLine()?.trim().orEmpty()

		if (entradaUsuario.isEmpty() || contrasena.isEmpty()) {
			println("Debe ingresar usuario y contrasena.")
			return
		}

		if (esLoginAdministrador(entradaUsuario, contrasena)) {
			usuarioActual = Usuario(
				id = 0,
				nombre = "Administrador",
				email = ADMIN_EMAIL,
				contrasena = ADMIN_PASSWORD,
				tipoUsuario = TipoUsuario.ADMIN
			)
			println("Sesion iniciada como administrador. Accediendo al panel mantenedor...")
			return
		}

		val emailNormalizado = entradaUsuario.lowercase()
		val usuarioRegistrado = usuariosRegistrados[emailNormalizado]

		if (usuarioRegistrado != null && usuarioRegistrado.contrasena == contrasena) {
			usuarioActual = usuarioRegistrado
			println("Bienvenido/a ${usuarioRegistrado.nombre} (${usuarioRegistrado.tipoUsuario.descripcion})")
		} else {
			println("Credenciales inválidas o usuario no registrado.")
		}
	}

	private fun procesarRegistroUsuario() {
		println("\n--- Registro de nuevo usuario ---")
		print("Nombre completo: ")
		val nombre = readLine()?.trim().orEmpty()

		print("Email (@duoc.cl / @duocuc.cl / otro): ")
		val email = readLine()?.trim().orEmpty().lowercase()

		print("Contrasena: ")
		val contrasena = readLine()?.trim().orEmpty()

		if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
			println("Todos los datos son obligatorios para el registro.")
			return
		}

		if (usuariosRegistrados.containsKey(email)) {
			println("Ya existe un usuario registrado con ese email.")
			return
		}

		try {
			val nuevoUsuario = Usuario(
				id = contadorUsuarios++,
				nombre = nombre,
				email = email,
				contrasena = contrasena
			)
			usuariosRegistrados[email] = nuevoUsuario
			usuarioActual = nuevoUsuario

			println("Registro exitoso. Bienvenido/a ${nuevoUsuario.nombre}!")
			println("Tipo de usuario detectado: ${nuevoUsuario.tipoUsuario.descripcion}")
		} catch (ex: IllegalArgumentException) {
			println("Error en los datos ingresados: ${ex.message}")
		}
	}

	private fun mostrarCatalogoComoInvitado() {
		imprimirCatalogoDetallado("Catalogo disponible para invitados")

		println("\nInicie sesion o registrese para acceder a mas funciones.")
		print("Presione Enter para volver al menu principal...")
		readLine()
	}

	private fun mostrarMenuAdministrador() {
		while (usuarioActual?.tipoUsuario == TipoUsuario.ADMIN) {
			println()
			print(FormatUtils.crearEncabezado("Panel de administracion"))
			println("1. Ver catalogo completo")
			println("2. Ver reporte de gestion")
			println("3. Cerrar sesion de administrador")
			println("0. Salir del sistema")
			print("Seleccione una opción: ")

			when (readLine()?.trim()) {
				"1" -> mostrarCatalogoAdministrador()
				"2" -> mostrarReporteAdministrador()
				"3" -> {
					println("Sesion de administrador finalizada.")
					usuarioActual = null
					return
				}
				"0" -> salirDelSistema()
				else -> println("Opcion no valida. Intente otra vez.")
			}
		}
	}

	private suspend fun mostrarMenuUsuario() {
		while (usuarioActual != null && usuarioActual?.tipoUsuario != TipoUsuario.ADMIN) {
			val usuario = usuarioActual ?: return

			println()
			print(FormatUtils.crearEncabezado("Panel de usuario - ${usuario.nombre}"))
			println("1. Mis libros")
			println("2. Devolver libro")
			println("3. Pedir libro")
			println("4. Buscar libro por titulo")
			println("5. Cerrar sesion")
			println("0. Salir del sistema")
			print("Seleccione una opcion: ")

			when (readLine()?.trim()) {
				"1" -> mostrarMisLibros(usuario)
				"2" -> devolverLibro(usuario)
				"3" -> solicitarLibro(usuario)
				"4" -> buscarLibroPorTitulo(usuario)
				"5" -> {
					println("Sesion cerrada para ${usuario.nombre}.")
					usuarioActual = null
					return
				}
				"0" -> salirDelSistema()
				else -> println("Opcion no valida. Intente otra vez.")
			}
		}
	}

	private fun mostrarMisLibros(usuario: Usuario) {
		println()
		print(FormatUtils.crearEncabezado("Mis libros"))
		val prestamosActivos = obtenerPrestamosActivos(usuario)

		if (prestamosActivos.isEmpty()) {
			println("No tienes prestamos activos en este momento.")
			print("Presione Enter para continuar...")
			readLine()
			return
		}

		prestamosActivos.forEachIndexed { index, prestamo ->
			println("${index + 1}. ${prestamo.libro.titulo}")
			println("   Precio prestamo: ${FormatUtils.formatearPrecio(prestamo.costoTotal)}")
			println("   Dias restantes: ${prestamo.diasRestantes()}")
			if (prestamo.estaAtrasado()) {
				println("    Mora acumulada: ${FormatUtils.formatearPrecio(prestamo.calcularMulta())}")
			}
			println()
		}

		print("Presione Enter para continuar...")
		readLine()
	}

	private suspend fun devolverLibro(usuario: Usuario) {
		println()
		print(FormatUtils.crearEncabezado("Devolver libro"))
		val prestamosActivos = obtenerPrestamosActivos(usuario)

		if (prestamosActivos.isEmpty()) {
			println("No tienes prestamos pendientes de devolucion.")
			print("Presione Enter para continuar...")
			readLine()
			return
		}

		prestamosActivos.forEachIndexed { index, prestamo ->
			println("${index + 1}. ${prestamo.libro.titulo}")
			println("   Dias restantes: ${prestamo.diasRestantes()}")
			if (prestamo.estaAtrasado()) {
				println("    Mora acumulada: ${FormatUtils.formatearPrecio(prestamo.calcularMulta())}")
			}
			println()
		}

		print("Seleccione el numero del libro a devolver (Enter para cancelar): ")
		val seleccion = readLine()?.trim()
		if (seleccion.isNullOrBlank()) {
			println("Operacion cancelada.")
			return
		}

		val indice = seleccion.toIntOrNull()
		if (indice == null || indice !in 1..prestamosActivos.size) {
			println("Seleccion invalida.")
			return
		}

		println("devolviendo libro, por favor espere...")
		delay(3000)

		when (val resultado = gestorPrestamos.devolverLibro(prestamosActivos[indice - 1].id)) {
			is ResultadoDevolucion.Exito -> {
				println("libro devuelto exitosamente!")
				println("Monto final del prestamo: ${FormatUtils.formatearPrecio(resultado.costoFinal)}")
				if (resultado.multa > 0) {
					println("Multa aplicada: ${FormatUtils.formatearPrecio(resultado.multa)}")
				}
			}
			is ResultadoDevolucion.Error -> println("No fue posible devolver el libro: ${resultado.mensaje}")
		}

		print("Presione Enter para continuar...")
		readLine()
	}

	private suspend fun solicitarLibro(usuario: Usuario) {
		println()
		print(FormatUtils.crearEncabezado("Pedir libro"))
		val catalogo = catalogoService.obtenerCatalogo()

		if (catalogo.isEmpty()) {
			println("No hay libros disponibles en el catalogo.")
			print("Presione Enter para continuar...")
			readLine()
			return
		}

		mostrarCatalogoParaSeleccion(catalogo)
		val libroSeleccionado = obtenerLibroPorSeleccion(catalogo) ?: return
		mostrarDetalleLibroParaUsuario(libroSeleccionado, usuario)
	}

	private suspend fun buscarLibroPorTitulo(usuario: Usuario) {
		println()
		print(FormatUtils.crearEncabezado("Buscar libro"))
		print("Ingrese titulo a buscar: ")
		val consulta = readLine()?.trim().orEmpty()

		if (consulta.isBlank()) {
			println("Debe ingresar un título para buscar.")
			return
		}

		val resultados = try {
			catalogoService.buscarLibrosPorTitulo(consulta)
		} catch (ex: Exception) {
			println("Error al buscar libros: ${ex.message}")
			return
		}

		if (resultados.isEmpty()) {
			println("No se encontraron libros que coincidan con '$consulta'.")
			return
		}

		println("\nResultados de busqueda:")
		mostrarCatalogoParaSeleccion(resultados)
		val libroSeleccionado = obtenerLibroPorSeleccion(resultados) ?: return
		mostrarDetalleLibroParaUsuario(libroSeleccionado, usuario)
	}

	private fun mostrarCatalogoParaSeleccion(catalogo: List<Libro>) {
		catalogo.forEachIndexed { index, libro ->
			println("${index + 1}. ${FormatUtils.formatearLibroParaCatalogo(libro)}")
		}
	}

	private fun obtenerLibroPorSeleccion(catalogo: List<Libro>): Libro? {
		print("Seleccione el numero del libro (Enter para cancelar): ")
		val seleccion = readLine()?.trim()
		if (seleccion.isNullOrBlank()) {
			println("Operacion cancelada.")
			return null
		}

		val indice = seleccion.toIntOrNull()
		if (indice == null || indice !in 1..catalogo.size) {
			println("Selección invalida.")
			return null
		}

		return catalogo[indice - 1]
	}

	private fun obtenerPrestamosActivos(usuario: Usuario): List<Prestamo> {
		return gestorPrestamos.obtenerPrestamosUsuario(usuario.id)
			.filter { it.estado.esActivo() }
	}

	private suspend fun mostrarDetalleLibroParaUsuario(libro: Libro, usuario: Usuario) {
		println()
		print(FormatUtils.formatearDetalleLibro(libro, usuario))
		procesarReservaLibro(libro, usuario)
	}

	private suspend fun procesarReservaLibro(libro: Libro, usuario: Usuario) {
		print("Reservar? [s]/n: ")
		val respuesta = readLine()?.trim().orEmpty()
		if (respuesta.equals("n", ignoreCase = true)) {
			println("Reserva cancelada.")
			return
		}

		println("reservando libro, por favor espere...")
		delay(3000)

		when (val resultado = gestorPrestamos.crearPrestamo(libro.id, usuario)) {
			is ResultadoPrestamo.Exito -> {
				val procesamiento = gestorPrestamos.procesarPrestamo(resultado.prestamo)
				if (procesamiento is ResultadoPrestamo.Error) {
					println("Ocurrio un problema al procesar el prestamo: ${procesamiento.mensaje}")
					print("Presione Enter para continuar...")
					readLine()
				} else {
					println("libro reservado exitosamente!, apriete cualquier tecla para continuar")
					readLine()
				}
			}
			is ResultadoPrestamo.Error -> {
				println("No fue posible realizar la reserva: ${resultado.mensaje}")
				print("Presione Enter para continuar...")
				readLine()
			}
			else -> {
				println("⚠Reserva en proceso: ${resultado.obtenerMensaje()}")
				print("Presione Enter para continuar...")
				readLine()
			}
		}
	}

	private fun mostrarCatalogoAdministrador() {
		imprimirCatalogoDetallado("Catalogo completo")
		print("Presione Enter para volver al panel administrador...")
		readLine()
	}

	private fun mostrarReporteAdministrador() {
		println()
		print(FormatUtils.crearEncabezado("Reporte integral del sistema"))
		val estadisticasCatalogo = catalogoService.obtenerEstadisticas()
		print(FormatUtils.formatearEstadisticasCatalogo(estadisticasCatalogo))

		val estadisticasPrestamos = gestorPrestamos.obtenerEstadisticasPrestamos()
		print(FormatUtils.formatearEstadisticasPrestamos(estadisticasPrestamos))

		val reportePrestamos = gestorPrestamos.generarReportePrestamos()
		if (reportePrestamos.totalPrestamos == 0) {
			println("Aun no se registran prestamos en el sistema.")
		} else {
			println("Resumen de actividad de prestamos:")
			println("- Prestamos devueltos: ${reportePrestamos.prestamosDevueltos}")
			println("- Ingresos totales por prestamos: ${FormatUtils.formatearPrecio(reportePrestamos.ingresosTotales)}")
			println("- Multas totales acumuladas: ${FormatUtils.formatearPrecio(reportePrestamos.multasTotales)}")

			if (reportePrestamos.librosMasPrestados.isNotEmpty()) {
				println("- Libros mas prestados:")
				reportePrestamos.librosMasPrestados.forEachIndexed { index, (libro, cantidad) ->
					println("   ${index + 1}. ${libro.titulo} (${cantidad} prestamo${if (cantidad == 1) "" else "s"})")
				}
			} else {
				println("- Aun no hay libros con historial de prestamos.")
			}

			val estadisticasMultas = reportePrestamos.estadisticasMultas
			println("- Promedio de multa por prestamo: ${FormatUtils.formatearPrecio(estadisticasMultas.promedioMultaPorPrestamo)}")
			println("- Promedio de multa por atraso: ${FormatUtils.formatearPrecio(estadisticasMultas.promedioMultaPorAtrasado)}")
			println("- Porcentaje de prestamos con atraso: ${"%.1f".format(estadisticasMultas.porcentajeAtrasos)}%")
		}

		print("Presione Enter para volver al panel administrador...")
		readLine()
	}

	private fun imprimirCatalogoDetallado(titulo: String) {
		println()
		print(FormatUtils.crearEncabezado(titulo))
		val catalogo = catalogoService.obtenerCatalogo()

		if (catalogo.isEmpty()) {
			println("No hay libros registrados en el catalogo.")
			return
		}

		catalogo.forEachIndexed { indice, libro ->
			println("${indice + 1}. ${libro.titulo}")
			println("   Autor: ${libro.autor}")
			println("   Categoria: ${libro.categoria}")
			println("   Anio publicacion: ${libro.anioPublicacion}")
			println("   Tipo: ${obtenerDescripcionTipoLibro(libro)}")
			println("   Precio base: ${FormatUtils.formatearPrecio(libro.precioBase)}")
			println("   Dias de prestamo: ${libro.diasPrestamo}")
			println()
		}
	}

	private fun obtenerDescripcionTipoLibro(libro: Libro): String {
		return when (libro) {
			is LibroFisico -> buildString {
				append("Fisico")
				append(" - Disponibles: ${libro.getEjemplaresDisponibles()}/${libro.cantidadEjemplares}")
				if (libro.esReferencia) append(" (referencia)")
			}

			is LibroDigital -> buildString {
				append("Digital")
				if (libro.drm) append(" (DRM)")
				if (libro.formatoArchivo.isNotBlank()) append(" - Formato: ${libro.formatoArchivo}")
			}

			else -> "Desconocido"
		}
	}

	private fun salirDelSistema(): Nothing {
		println("Gracias por usar BookSmart. ¡Hasta pronto!")
		exitProcess(0)
	}

	private fun esLoginAdministrador(usuario: String, contrasena: String): Boolean {
		val normalizado = usuario.lowercase()
		return (normalizado == "admin" || normalizado == ADMIN_EMAIL) && contrasena == ADMIN_PASSWORD
	}

	companion object {
		private const val ADMIN_EMAIL = "admin@booksmart.com"
		private const val ADMIN_PASSWORD = "booksmart"
	}
}
