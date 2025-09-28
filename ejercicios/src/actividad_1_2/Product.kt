package actividad_1_2

import kotlinx.serialization.Serializable

@Serializable
data class Product(val nombre: String, val precio: Int, val categoria: String)
