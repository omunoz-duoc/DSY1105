package actividad_1_3

open class Persona(var nombre: String, val rut: String, val fechaNacimiento: String, var edad: Int) : Informable {
	override fun toString(): String {
		return "Persona(nombre='$nombre', rut='$rut', fechaNacimiento='$fechaNacimiento', edad=$edad)"
	}
	
	open fun mostrarInfo() {
		println("Nombre: $nombre")
		println("RUT: $rut")
		println("Fecha de Nacimiento: $fechaNacimiento")
		println("Edad: $edad")
	}
	
	override fun informar() {
		println("Información de la persona:")
		mostrarInfo()
	}
}