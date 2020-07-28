package com.diaz_danny.examenibappmoviles

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_actualizar_mensaje.*
import models.ChatGroup
import models.Message

class ActualizarMensajeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_mensaje)

        val grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)
        val mensajePosition = intent.getIntExtra("mensajePosition", -1)


        textViewActualizarMensajeId.text = ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).message_id.toString()
        textViewActualizarMensajeFecha.text = ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).date_creation
        textViewActualizarMensajeModificado.text = if(ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).modified) "Si" else "No"
        textViewActualizarMensajeEmisor.text = ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).sender
        editTextActualizarMensajeContenido.setText(ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).content)


        buttonActualizarMensajeActualizar.setOnClickListener({
            ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).content = editTextActualizarMensajeContenido.text.toString()
            ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(mensajePosition).modified = true

            setResult(Activity.RESULT_OK)
            finish()
        })

        buttonActualizarMensajeCancelar.setOnClickListener({

            setResult(Activity.RESULT_CANCELED)
            finish()
        })


    }
}
