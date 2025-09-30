package cl.duoc.dsy1105.booksmart

import cl.duoc.dsy1105.booksmart.repository.LibroRepository
import cl.duoc.dsy1105.booksmart.service.*
import cl.duoc.dsy1105.booksmart.ui.MenuHandler
import kotlinx.coroutines.runBlocking

/**
 * Punto de entrada principal del sistema BookSmart.
 * Inicializa todos los servicios y ejecuta la aplicación interactiva.
 */
fun main() = runBlocking {
    try {
        println("=== BookSmart - Sistema de Gestión de Biblioteca ===")
        println("Inicializando sistema...")
        
        // Inicializar servicios
        val repository = LibroRepository()
        val catalogoService = CatalogoService(repository)
        val gestorPrestamos = GestorPrestamos(repository)
        
        // Inicializar interfaz de usuario
        val menuHandler = MenuHandler(catalogoService, gestorPrestamos)
        
        println("Sistema inicializado correctamente ✅")
        
        // Iniciar aplicación interactiva
        menuHandler.iniciar()
        
    } catch (e: Exception) {
        println("Error fatal en la aplicacion: ${e.message}")
        e.printStackTrace()
    }
}



