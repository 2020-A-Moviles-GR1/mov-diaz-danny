package com.diaz_danny.DeusGallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import kotlinx.android.synthetic.main.activity_http.*

class HttpActivity : AppCompatActivity() {

    val urlPrincipal = "http://192.168.1.5:1337"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)

        buttonHttpObtener.setOnClickListener{
            obtenerUsuarios()
        }
    }

    private fun obtenerUsuarios() {

        val url = urlPrincipal + "/usuario"

        url
            .httpGet()
            .responseString{
                request, response, result ->

                when(result){
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        //Log.i("http-klaxon", "Data: ${data}")


                        val usuarios = Klaxon().parseArray<UsuarioHttp>(data)


                        if(usuarios != null){
                            usuarios.forEach{
                                Log.i(
                                    "http-klaxon",
                                    "\nUSUARIO->Nombre: ${it.nombre}\n"

                                )

                                if(it.pokemons is List<*>){
                                    if(it.pokemons!!.size > 0){
                                        it.pokemons!!.forEach{
                                            it as PokemonHttp

                                            Log.i(
                                                "http-klaxon",
                                                "POKEMON_DE_USUARIO ${it.usuario}->Nombre: ${it.nombre}\n"
                                            )
                                        }
                                    }
                                }

                            }
                        }

                        Log.i("http-klaxon","\n\nCONSULTANDO POKEMONS\n\n")
                        obtenerPokemons()
                    }
                    is com.github.kittinunf.result.Result.Failure -> {
                        val ex = result.getException()
                        Log.i("http-klaxon", "Error: ${ex.message}")
                    }
                }

            }

    }

    private fun obtenerPokemons() {

        val url = urlPrincipal + "/pokemon"

        url
            .httpGet()
            .responseString{
                    request, response, result ->

                when(result){
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        //Log.i("http-klaxon", "Data: ${data}")

                        val pokemons = Klaxon()
                            .converter(PokemonHttp.myConverter)
                            .parseArray<PokemonHttp>(data)

                        /*
                        val pokemons = Klaxon().parseArray<PokemonHttp>(data)
                         */

                        if(pokemons != null){
                            pokemons.forEach{
                                Log.i(
                                    "http-klaxon",
                                    "POKEMON:Nombre: ${it.nombre}"
                                            + ", Usuario: ${it.usuario}\n"

                                )



                            }
                        }
                    }
                    is com.github.kittinunf.result.Result.Failure -> {
                        val ex = result.getException()
                        Log.i("http-klaxon", "Error: ${ex.message}")
                    }
                }

            }

    }





    /*
        val pokemonString = """
            {
            "createdAt": 1597671444841,
            "updatedAt": 1597672206086,
            "id": 1,
            "nombre": "Pikachu",
            "usuario": 1
           }
        """.trimIndent()

        val pokemonINstancia = Klaxon().parse<PokemonHttp>(pokemonString)

        if (pokemonINstancia != null) {
            Log.i("http-klaxon",
                "Nombre: ${pokemonINstancia.nombre} " +
                        "FechaCreacion: ${pokemonINstancia.fechaCreacion} " +
                        "FechaActulizacion: ${pokemonINstancia.fechaActualizacion} "
            )
        }

        */
}