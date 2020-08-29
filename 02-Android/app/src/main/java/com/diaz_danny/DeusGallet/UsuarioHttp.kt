package com.diaz_danny.DeusGallet

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import java.util.*

class UsuarioHttp(
    var id: Int,
    var createdAt: Long,
    var updatedAt: Long,
    var cedula: String,
    var nombre: String,
    var correo: String,
    var estadoCivil: String,
    var password: String,
    var pokemons: ArrayList<PokemonHttp>? = null//ArrayList<PokemonHttp>? Any? = null

) {



    companion object {
        val myConverter = object: Converter {
            override fun canConvert(cls: Class<*>) = cls == UsuarioHttp::class.java

            override fun toJson(value: Any): String {
                val usuario = value as UsuarioHttp


                return """
                  {
                    
                    "id": ${usuario.id},
                    "createdAt": ${usuario.createdAt},
                    "updatedAt": ${usuario.updatedAt},
                    "cedula": "${usuario.cedula}",
                    "nombre": "${usuario.nombre}",
                    "correo": "${usuario.correo}",
                    "estadoCivil": "${usuario.estadoCivil}",
                    "password": "${usuario.password}",
                    "pokemons": ${Klaxon().toJsonString(usuario.pokemons as List<*>)}
                   }


                    
                    }
                """.trimMargin()



            }



            override fun fromJson(jv: JsonValue) : UsuarioHttp {

                return UsuarioHttp(
                    jv.objInt("id"),
                    jv.obj?.get("createdAt") as Long,
                    jv.obj?.get("updatedAt") as Long,
                    jv.objString("cedula"),
                    jv.objString("nombre"),
                    jv.objString("correo"),
                    jv.objString("estadoCivil"),
                    jv.objString("password"),
                    Klaxon().parseFromJsonArray<PokemonHttp>(jv.obj?.get("pokemons") as JsonArray<*>) as ArrayList<PokemonHttp>

                )


            }




        }
    }




        var fechaCreacion: Date
    var fechaActualizacion: Date

    init {
        fechaCreacion = createdAt.let { Date(it) }
        fechaActualizacion = updatedAt.let { Date(it) }
    }

    override fun toString(): String {
        return "UsuarioHttp(id=$id, createdAt=$createdAt, updatedAt=$updatedAt, cedula='$cedula', nombre='$nombre', correo='$correo', estadoCivil='$estadoCivil', password='$password', fechaCreacion=$fechaCreacion, fechaActualizacion=$fechaActualizacion)" + (if(pokemons!=null) "pokemons=$pokemons" else "")
    }




}