package actividad_1_3

class Empleado(
	nombre: String,
	rut: String,
	fechaNacimiento: String,
	edad: Int,
	var cargo: String,
	var sueldo: Int
): Persona(nombre, rut, fechaNacimiento, edad), Informable {
	override fun toString(): String {
		val personaInfo = super.toString()
		return "Empleado(nombre='$nombre', rut='$rut', fechaNacimiento='$fechaNacimiento', edad='$edad' cargo='$cargo'," +
				" sueldo=$sueldo, $personaInfo)"
	}
	
	override fun mostrarInfo() {
		super.mostrarInfo()
		println("Cargo: $cargo")
		println("Sueldo: $sueldo")
	}
	
	fun calcularBonificacion(): Int {
		return (sueldo * 0.1).toInt()
	}
	
	override fun informar() {
		println("Información del empleado:")
		mostrarInfo()
		println("Bonificación: ${calcularBonificacion()}")
	}
	
}