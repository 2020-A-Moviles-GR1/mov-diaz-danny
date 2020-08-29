package com.diaz_danny.DeusGallet

import com.beust.klaxon.*
import java.util.*
import kotlin.reflect.typeOf

class PokemonHttp(
    var id: Int,
    var createdAt: Long,
    var updatedAt: Long,
    var nombre: String,
    var usuario: Any?  // Any? Int
){


    companion object {
        val myConverter = object: Converter {
            override fun canConvert(cls: Class<*>) = cls == PokemonHttp::class.java

            override fun toJson(value: Any): String {

                val pokemon = value as PokemonHttp

                var usuario: Any?

                if(pokemon.usuario is Int){
                    usuario = pokemon.usuario
                }else if(pokemon.usuario is UsuarioHttp){
                    usuario = Klaxon().toJsonString(pokemon.usuario as UsuarioHttp)
                }else{
                    throw Error("No soportado")
                }

                return """
                  {

                    "id": ${pokemon.id},
                    "createdAt": ${pokemon.createdAt},
                    "updatedAt": ${pokemon.updatedAt},
                    "nombre": "${pokemon.nombre}",
                    "usuario": ${usuario}
                   }
                """.trimMargin()


            }



            override fun fromJson(jv: JsonValue) : PokemonHttp {



                if(jv.obj?.get("usuario") is JsonObject){

                    return PokemonHttp(
                        jv.objInt("id"),
                        jv.obj?.get("createdAt") as Long,
                        jv.obj?.get("updatedAt") as Long,
                        jv.objString("nombre"),
                        Klaxon().parseFromJsonObject<UsuarioHttp>(jv.obj?.get("usuario") as JsonObject)

                    )
                }else if(jv.obj?.get("usuario") is Int){
                    return PokemonHttp(
                        jv.objInt("id"),
                        jv.obj?.get("createdAt") as Long,
                        jv.obj?.get("updatedAt") as Long,
                        jv.objString("nombre"),
                        jv.objInt("usuario")
                    )
                }else{
                    throw Error("No soportado")
                }


            }




        }
    }



    var fechaCreacion: Date
    var fechaActualizacion: Date

    init {
        fechaCreacion = Date(createdAt)
        fechaActualizacion = Date(updatedAt)
    }

    override fun toString(): String {
        return "PokemonHttp(id=$id, createdAt=$createdAt, updatedAt=$updatedAt, nombre='$nombre', usuario=$usuario, fechaCreacion=$fechaCreacion, fechaActualizacion=$fechaActualizacion)"
    }


}