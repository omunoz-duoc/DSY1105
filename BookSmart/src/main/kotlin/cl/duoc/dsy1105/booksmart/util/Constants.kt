package cl.duoc.dsy1105.booksmart.util

/**
 * Constantes del sistema BookSmart.
 */
object Constants {
    
    // === CONFIGURACIONES DEL SISTEMA ===
    const val APP_NAME = "BookSmart"
    const val APP_VERSION = "1.0.0"
    const val APP_DESCRIPTION = "Sistema de Gestión de Libros - DUOC UC"
    
    // === CONFIGURACIONES DE PRÉSTAMOS ===
    const val DIAS_PRESTAMO_DEFAULT = 14
    const val DIAS_PRESTAMO_LIBRO_FISICO = 7
    const val DIAS_PRESTAMO_LIBRO_DIGITAL = 10
    const val DIAS_PRESTAMO_REFERENCIA = 0
    
    // === MULTAS Y COSTOS ===
    const val MULTA_POR_DIA_ATRASO = 100 // $100 por día
    const val PRECIO_MINIMO_LIBRO = 0
    const val PRECIO_MAXIMO_LIBRO = 100000
    
    // === LÍMITES DE USUARIO ===
    const val MAX_ATRASOS_PERMITIDOS = 3
    const val MAX_LIBROS_SIMULTANEOS = 5
    const val MAX_DIAS_PRESTAMO = 30
    
    // === DESCUENTOS POR TIPO DE USUARIO ===
    const val DESCUENTO_ESTUDIANTE = 0.10 // 10%
    const val DESCUENTO_DOCENTE = 0.15    // 15%
    const val DESCUENTO_EXTERNO = 0.0     // 0%
    
    // === CREDENCIALES DE ADMINISTRADOR ===
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_PASSWORD = "booksmart"
    const val ADMIN_EMAIL = "admin@booksmart.com"
    
    // === DOMINIOS DE EMAIL INSTITUCIONALES ===
    const val DOMINIO_ESTUDIANTE = "@duocuc.cl"
    const val DOMINIO_DOCENTE = "@duoc.cl"
    
    // === MENSAJES DE SISTEMA ===
    object Mensajes {
        const val BIENVENIDA = "¡Bienvenido a $APP_NAME!"
        const val PRESTAMO_EXITOSO = "Libro reservado exitosamente!"
        const val DEVOLUCION_EXITOSA = "Libro devuelto exitosamente!"
        const val ERROR_LIBRO_NO_DISPONIBLE = "El libro no está disponible para préstamo"
        const val ERROR_USUARIO_CON_ATRASOS = "No puede realizar préstamos debido a atrasos pendientes"
        const val ERROR_LIBRO_REFERENCIA = "Los libros de referencia no se pueden prestar"
        const val ERROR_CREDENCIALES_INVALIDAS = "Email o contraseña incorrectos"
        const val PROCESANDO_PRESTAMO = "Procesando préstamo, por favor espere..."
        const val PROCESANDO_DEVOLUCION = "Devolviendo libro, por favor espere..."
        const val VALIDANDO_DATOS = "Validando datos del préstamo..."
    }
    
    // === CONFIGURACIONES DE UI ===
    object UI {
        const val ANCHO_PANTALLA = 80
        const val ALTO_MENU = 25
        const val TIEMPO_ESPERA_PROCESAMIENTO = 3000L // 3 segundos
        val SEPARADOR = "=".repeat(ANCHO_PANTALLA)
        val SEPARADOR_MENOR = "-".repeat(ANCHO_PANTALLA / 2)
        
        // Símbolos para mostrar información de libros
        const val SIMBOLO_DISPONIBLE = "✓"
        const val SIMBOLO_NO_DISPONIBLE = "✗"
        const val SIMBOLO_REFERENCIA = "(referencia)"
        const val SIMBOLO_DRM = "(DRM)"
        const val SIMBOLO_FISICO = "[F]"
        const val SIMBOLO_DIGITAL = "[D]"
    }
    
    // === CÓDIGOS DE ERROR ===
    object ErrorCodes {
        const val LIBRO_NO_ENCONTRADO = "ERR_001"
        const val USUARIO_NO_ENCONTRADO = "ERR_002"
        const val PRESTAMO_INVALIDO = "ERR_003"
        const val VALIDACION_FALLIDA = "ERR_004"
        const val CREDENCIALES_INVALIDAS = "ERR_005"
        const val ACCESO_DENEGADO = "ERR_006"
        const val RECURSO_NO_DISPONIBLE = "ERR_007"
    }
    
    // === CONFIGURACIONES DE FORMATO ===
    object Formato {
        const val FORMATO_FECHA = "dd/MM/yyyy"
        const val FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm"
        const val FORMATO_PRECIO = "$#,###"
        const val FORMATO_PORCENTAJE = "#%"
    }
    
    // === VALIDACIONES ===
    object Validaciones {
        const val MIN_LENGTH_NOMBRE = 2
        const val MAX_LENGTH_NOMBRE = 50
        const val MIN_LENGTH_PASSWORD = 6
        const val MAX_LENGTH_PASSWORD = 20
        const val MIN_LENGTH_TITULO = 1
        const val MAX_LENGTH_TITULO = 100
        val REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}