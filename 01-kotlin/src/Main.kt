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
    val arregloCumpleanos: ArrayList<Int> = arrayListOf(30, 31, 22, 23, 20)
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

    val respuestaAny:Boolean = arregloCumpleanos
            .any{ iterador:Int ->

                return@any iterador < 25
            }

    println(respuestaAny)

    val respuestaAll:Boolean = arregloCumpleanos
            .any{ iterador:Int ->

                return@any iterador > 18
            }

    println(respuestaAll)

    val respuestaReduce:Int = arregloCumpleanos
            .reduce { acumulador, iteracion ->

                return@reduce acumulador + iteracion

            }/arregloCumpleanos.size

    println(respuestaReduce)

    val respuestaFold:Int = arregloCumpleanos
            .fold(
                    100,
                    {acumulador, iteracion ->
                        return@fold acumulador - iteracion
                    }
            )

    println(respuestaFold)

    // para cambiar el orden de las iteraciones
    // existe reduce right, left
    // existe fold right, left

    val vidaActual: Double = arregloCumpleanos
            .map { it * 0.8 }
            .filter { it > 18 }  // el héroe tiene un item que no recibe daño de 18 para abajo
            .fold(
                    100.00,
                    { acumulador, iterador -> acumulador - iterador }
            )
            .also { println(it) }

    // No usar For a menos que sea necesario








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

abstract class NumerosJava{
    val numeroUno:Int  // public es por defecto
    private val numeroDos:Int
    constructor(uno:Int, dos:Int){
        numeroUno = uno
        numeroDos = dos
    }
}

abstract class Numeros(  //val clsN = Numeros()
    val numeroUno:Int,
    val numeroDos:Int
){

}


class Suma(
        private val uno: Int,
        private val dos: Int
):Numeros(uno, dos){  //herencia

    public fun sumar(): Int{
        return this.numeroUno + this.numeroDos
    }
}

