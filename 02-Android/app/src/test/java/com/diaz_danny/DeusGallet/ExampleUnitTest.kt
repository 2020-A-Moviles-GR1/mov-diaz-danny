package com.diaz_danny.DeusGallet

import com.beust.klaxon.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun parseUserHttp_isCorrect() {

        val usuarioHttp = """
            {
            "createdAt": 1597671444841,
            "updatedAt": 1597672206086,
            "id": 1,
            "cedula": "1720254224",
            "nombre": "Mi nombre",
            "correo": "danny.diaz@epn.edu.ec",
            "estadoCivil": "Unión libre",
            "password": "Snel1025",
            "pokemons": [
                {
                    "createdAt": 1597671444841,
                    "updatedAt": 1597672206086,
                    "id": 1,
                    "nombre": "Pikachu",
                    "usuario": 1,
                    "batalla": 2
                }
            ]
           }
        """.trimIndent()




        val result = Klaxon()
            .converter(UsuarioHttp.myConverter)
            .parse<UsuarioHttp>(usuarioHttp)

        println(result)


        println(UsuarioHttp.myConverter.toJson(result as Any))

        // it.pokemons is List<*>


        assertEquals(4, 2 + 2)
    }


    @Test
    fun parsePokemonHttp_isCorrect() {


        val pokemonHttp = """
            {
            "id": 1,
            "createdAt": 1597671444841,
            "updatedAt": 1597672206086,
            "nombre": "Pikachu",
            "usuario":  {
                    "createdAt": 1591111111111,
                    "updatedAt": 1591111111112,
                    "id": 1,
                    "cedula": "1720254224",
                    "nombre": "Mi nombre",
                    "correo": "danny.diaz@epn.edu.ec",
                    "estadoCivil": "Unión libre",
                    "password": "Snel1025"
            }
            
           }
        """.trimIndent()




        val result = Klaxon()
            .converter(PokemonHttp.myConverter)
            .parse<PokemonHttp>(pokemonHttp)

        println(result)


       println(PokemonHttp.myConverter.toJson(result as Any))



        assertEquals(4, 2 + 2)
    }
}
