import java.util.*

fun main(args:Array<String>){
    // se basa en funciones ya no en clases
    print("Hola")

    // int edad = 31;

    // Kotlin tiene variables
    // mutables: no usar casi nunca, si estamos reasignando estamos escribiendo mal el código
    // y puede haber otra forma de escribir el código
    var edadProfesor = 31

    var edadCachorro:Int
    edadCachorro = 3
    edadProfesor = 32;
    edadCachorro = 4;


    // inmutable: recomendable usar siempre
    val numeroCuenta = 123456


    // Tipos variables, esto es innecesario poner asi que se debería borrar
    val nombreProfesor: String = "Vicente Adrian"
    val sueldo: Double = 12.20
    val apellidosProfesor = 'a'
    val fechaNacimiento = Date() // new Date()

    when (sueldo){
        12.20 -> print("Sueldo normal")
        -12.20 -> print("Sueldo negativo")
        else -> print("No se reconoce el sueldo")
    }

    val esSueldoMayorAlEstablecido = if(sueldo == 12.20) true else false

    calcularSueldo(1000.00, 14.00)
    calcularSueldo(sueldo=800.00, tasa=16.00)

}

fun calcularSueldo(
        tasa: Double = 12.00,
        sueldo: Double,
        calculoEspecial: Int? = null
): Double {


    return if(calculoEspecial != null){
        sueldo * tasa * calculoEspecial
    }else{
        sueldo * tasa
    }


}

fun imprimirMensaje(): Unit {  // Unit = void
    println("")
}




