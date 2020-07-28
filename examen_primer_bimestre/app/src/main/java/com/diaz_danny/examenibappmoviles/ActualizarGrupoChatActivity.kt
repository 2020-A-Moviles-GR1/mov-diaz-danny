package com.diaz_danny.examenibappmoviles

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_actualizar_grupo_chat.*
import models.ChatGroup

class ActualizarGrupoChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_grupo_chat)

        val grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)
        if(grupoChatPosition == -1){
            finish()
            return
        }

        textViewActualizarGrupoChatId.text = ChatGroup.companion_chat_groups.get(grupoChatPosition).chat_id.toString()
        textViewActualizarGrupoChatFecha.text = ChatGroup.companion_chat_groups.get(grupoChatPosition).date_creation
        editTextActualizarGrupoChatNombre.setText(ChatGroup.companion_chat_groups.get(grupoChatPosition).name_group)
        editTextActualizarGrupoChatContrasenia.setText(ChatGroup.companion_chat_groups.get(grupoChatPosition).password)

        spinnerActualizarGrupoChatActivo.setSelection(if(ChatGroup.companion_chat_groups.get(grupoChatPosition).active) 0 else 1)

        buttonActualizarGrupoChatActualizar.setOnClickListener({
            ChatGroup.companion_chat_groups.get(grupoChatPosition).name_group = editTextActualizarGrupoChatNombre.text.toString()
            ChatGroup.companion_chat_groups.get(grupoChatPosition).password = editTextActualizarGrupoChatContrasenia.text.toString()
            ChatGroup.companion_chat_groups.get(grupoChatPosition).active = spinnerActualizarGrupoChatActivo.selectedItemPosition == 0


            setResult(Activity.RESULT_OK)
            finish()
        })

        buttonActualizarGrupoChatCancelar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })


    }
}
