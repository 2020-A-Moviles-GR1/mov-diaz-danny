package com.diaz_danny.examenibappmoviles

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.diaz_danny.examenibappmoviles.models.Parameters
import com.github.kittinunf.fuel.httpPut
import kotlinx.android.synthetic.main.activity_actualizar_grupo_chat.*
import models.ChatGroup

class ActualizarGrupoChatActivity : AppCompatActivity() {

    var grupoChatPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_grupo_chat)

        grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)
        if(grupoChatPosition == -1){
            finish()
            return
        }

        textViewActualizarGrupoChatId.text = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.chat_id.toString()
        textViewActualizarGrupoChatFecha.text = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.date_creation
        editTextActualizarGrupoChatNombre.setText(ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.name_group)
        editTextActualizarGrupoChatContrasenia.setText(ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.password)

        spinnerActualizarGrupoChatActivo.setSelection(if(ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.active!!) 0 else 1)

        buttonActualizarGrupoChatActualizar.setOnClickListener({
            ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.name_group = editTextActualizarGrupoChatNombre.text.toString()
            ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.password = editTextActualizarGrupoChatContrasenia.text.toString()
            ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.active = spinnerActualizarGrupoChatActivo.selectedItemPosition == 0

            val parametrosChatGroup = listOf(
                "name_group" to ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.name_group,
                "password" to ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.password,
                "active" to "${ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.active}"
            )

            ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.id?.let { it1 ->
                actualizarGrupoChat(parametrosChatGroup as List<Pair<String, String>>,
                    it1
                )
            }


        })

        buttonActualizarGrupoChatCancelar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })


    }

    private fun actualizarGrupoChat(parametrosChatGroup: List<Pair<String, String>>, id: Int) {
        val url = Parameters.urlPrincipal + "/chatgroup/$id"



        url
            .httpPut(parametrosChatGroup)
            .responseString {
                    req, res, result ->
                when(result){
                    is com.github.kittinunf.result.Result.Failure -> {
                        val error = result.getException()
                        Log.i(CrearGrupoChatActivity.TAG, "Error: $error")
                    }
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        Log.i(CrearGrupoChatActivity.TAG, "UPDATED Group Chat: $data")

                        val chatGroup = Klaxon().parse<ChatGroup>(data)

                        if (chatGroup != null) {
                            ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.updatedAt = chatGroup.updatedAt

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
