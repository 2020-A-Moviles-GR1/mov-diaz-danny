import java.util.*

fun main(args:Array<String>) {
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


    // clase 15-06-2020
    val arregloConstante: Array<Int> = arrayOf(1,2,3)
    val arregloCumpleanos: ArrayList<Int> = arrayListOf(30, 31, 32)
    print(arregloCumpleanos)
    arregloCumpleanos.add(24)
    print(arregloCumpleanos)
    arregloCumpleanos.remove(30)
    print(arregloCumpleanos)

    // El forEach no devuelve nada (unit)

    arregloCumpleanos
            .forEach{
                println("Valor de la iteración $it")  // + it
            }

    val res = arregloCumpleanos
            .forEach { valorIteracion: Int ->
                println("Valor de la iteración $valorIteracion")
                println("Valor con -1 = ${valorIteracion * -1}")

            }


    arregloCumpleanos
            .forEach (
                    { valorIteracion: Int ->
                        println("Valor de la iteración $valorIteracion")
                    }
            )

    arregloCumpleanos
            .forEachIndexed{ index:Int, it:Int ->
                        println("-Valor de la iteración $it")
                        println("--Valor de la index $index")
                    }


    println(res)  // unit


    val arregloCumpleanosMenos1 = arregloCumpleanos
            .map{iterador:Int ->
                iterador * -1
            }
    println(arregloCumpleanosMenos1)
    println(arregloCumpleanos)


    val respuestaMapDos: List<String> = arregloCumpleanos
            .map{iterador:Int ->
                val nuevoVal = iterador * -1
                val otroVal = nuevoVal * 2
                return@map otroVal.toString()
            }
    println(respuestaMapDos)
    println(arregloCumpleanos)


    val respuestaMayores23 =
            arregloCumpleanos.filter {iteracion:Int ->
                val esMayorA23 = iteracion > 23
                return@filter esMayorA23
            }

    val respuestaMayores23Linea =
            arregloCumpleanos.filter {
                iteracion:Int -> iteracion > 23
            }

    println(respuestaMayores23)
    println(respuestaMayores23Linea)







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




