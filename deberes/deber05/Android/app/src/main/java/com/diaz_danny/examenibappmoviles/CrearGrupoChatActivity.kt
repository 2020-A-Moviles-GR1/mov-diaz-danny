package com.diaz_danny.examenibappmoviles

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.Klaxon
import com.diaz_danny.examenibappmoviles.models.Parameters.Companion.urlPrincipal
import com.github.kittinunf.fuel.httpPost
import kotlinx.android.synthetic.main.activity_crear_grupo_chat.*
import models.ChatGroup
import java.text.SimpleDateFormat
import java.util.*

class CrearGrupoChatActivity : AppCompatActivity() {

    private var delay: Float = 0.0f

    companion object {
        val TAG = "CrearGrupoChatA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_grupo_chat)


        val id_generada = intent.getIntExtra("id", -1)
        if(id_generada == -1){
            finish()
            return
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val today = sdf.format(Date())

        textViewCrearGrupoChatId.text = id_generada.toString()
        textViewCrearGrupoChatFecha.text = today



        buttonCrearGrupoChatCrear.setOnClickListener({
            val name_group = editTextCrearGrupoChatNombre.text.toString()
            val active = spinnerCrearGrupoChatActivo.selectedItemPosition == 0
            val password = editTextCrearGrupoChatContrasenia.text.toString()

            delay = Math.random().toFloat()

            val parametrosChatGroup = listOf(
                "chat_id" to "$id_generada",
                "name_group" to name_group,
                "date_creation" to today,
                "active" to "$active",
                "mean_delay" to "$delay",
                "password" to password
            )


            crearGrupoChat(parametrosChatGroup)


        })

        buttonCrearGrupoChatCancelar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })




    }

    private fun crearGrupoChat(parametrosUsuario: List<Pair<String, String>>) {
        val url = urlPrincipal + "/chatgroup"



        url
            .httpPost(parametrosUsuario)
            .responseString {
                    req, res, result ->
                when(result){
                    is com.github.kittinunf.result.Result.Failure -> {
                        val error = result.getException()
                        Log.i(TAG, "Error: $error")
                    }
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        Log.i(TAG, "New Group Chat: $data")

                        Klaxon().parse<ChatGroup>(data)?.let {
                            ChatGroup.companion_chat_groups?.add(
                                it
                            )
                        }

                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }

            }
    }

}
