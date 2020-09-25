package com.diaz_danny.examenibappmoviles

import android.app.Activity
import androidx.appcompat.app .AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.diaz_danny.examenibappmoviles.models.Parameters
import com.github.kittinunf.fuel.httpPut
import kotlinx.android.synthetic.main.activity_actualizar_mensaje.*
import models.ChatGroup
import models.Message

class ActualizarMensajeActivity : AppCompatActivity() {

    var grupoChatPosition:Int = -1
    var mensajePosition:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_mensaje)

        grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)
        mensajePosition = intent.getIntExtra("mensajePosition", -1)


        textViewActualizarMensajeId.text = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(mensajePosition)?.message_id.toString()
        textViewActualizarMensajeFecha.text = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(mensajePosition)?.date_creation
        textViewActualizarMensajeModificado.text = if(ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(mensajePosition)?.modified!!) "Si" else "No"
        textViewActualizarMensajeEmisor.text = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(mensajePosition)?.sender
        editTextActualizarMensajeContenido.setText(ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(mensajePosition)?.content)


        buttonActualizarMensajeActualizar.setOnClickListener({
            ChatGroup.companion_chat_groups!!.get(grupoChatPosition).messages?.get(mensajePosition)?.content = editTextActualizarMensajeContenido.text.toString()
            ChatGroup.companion_chat_groups!!.get(grupoChatPosition).messages?.get(mensajePosition)?.modified = true

            val parametrosChatGroup = listOf(
                "content" to ChatGroup.companion_chat_groups!!.get(grupoChatPosition).messages?.get(mensajePosition)?.content,
                "modified" to "true"
            )

            ChatGroup.companion_chat_groups!!.get(grupoChatPosition).messages?.get(mensajePosition)?.id?.let { it1 ->
                actualizarMessage(
                    parametrosChatGroup as List<Pair<String, String>>,
                    it1
                )
            }

        })

        buttonActualizarMensajeCancelar.setOnClickListener({

            setResult(Activity.RESULT_CANCELED)
            finish()
        })


    }

    companion object {
        val TAG = "ActualizarMensajeA"
    }

    private fun actualizarMessage(parametrosMessage: List<Pair<String, String>>, id: Int) {
        val url = Parameters.urlPrincipal + "/message/$id"



        url
            .httpPut(parametrosMessage)
            .responseString {
                    req, res, result ->
                when(result){
                    is com.github.kittinunf.result.Result.Failure -> {
                        val error = result.getException()
                        Log.i(TAG, "Error: $error")
                    }
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        Log.i(TAG, "UPDATED Message: $data")

                        val message = Klaxon().parse<Message>(data)

                        if (message != null) {
                            ChatGroup.companion_chat_groups.get(grupoChatPosition)?.messages?.get(mensajePosition)?.updatedAt = message.updatedAt

                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{

                            Toast.makeText(this, "No se actualizó", Toast.LENGTH_LONG).show()
                        }

                    }
                }

            }
    }
}
