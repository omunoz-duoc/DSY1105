/** GUIA 3 */

package actividad_1_2

fun main(args: Array<String>) {
    val inventario: List<Product> = getProductsList()
    
	// 1. Búsqueda de un producto
	val laptop = findProductByName("laptop", inventario)
	val bolso = findProductByName("zapatillas", inventario)
	println(if(laptop !== null) "Producto encontrado: $laptop" else "Producto no encontrado")
	println(if(bolso !== null) "Producto encontrado: $bolso" else "Producto no encontrado")
	
	// 2. Cálculo del precio promedio del inventario (con precio)
	val precioPromedio = calAveragePrice(inventario)
	println("El precio promedio del inventario es: $precioPromedio")
	
	// 3. Filtro del inventario por categoría
	val categoryToFilter = "accesorios"
	val proElectronicos = filterProductsByCategory(categoryToFilter, inventario)
	
	// 4. Mapear solo los nombres de los productos
	val nombresProductos = inventario.map { it.nombre }
	
	// 5. Imprimir en pantalla
	// 5.1 resultado filtro
	when (val cantProductosElec = proElectronicos.size) {
		0 -> {
			println("No se encontraron productos en la categoría '$categoryToFilter'")
		}
		1 -> {
			println("Se encontró 1 producto en la categoría '$categoryToFilter': $proElectronicos")
		}
		else -> {
			println("Se encontraron $cantProductosElec productos en la categoría '$categoryToFilter': ${proElectronicos.map { it.nombre } }")
		}
	}
	
	// 5.2 nombres productos
	println("Nombres de los productos en el inventario: $nombresProductos")
}

