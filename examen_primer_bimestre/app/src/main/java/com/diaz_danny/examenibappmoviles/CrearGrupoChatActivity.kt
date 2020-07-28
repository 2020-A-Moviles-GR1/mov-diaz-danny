package com.diaz_danny.examenibappmoviles

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_crear_grupo_chat.*
import models.ChatGroup
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class CrearGrupoChatActivity : AppCompatActivity() {

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


            ChatGroup.companion_chat_groups.add(ChatGroup(
                id_generada, name_group, today, active,
                Math.random(),
                password
            ))
            setResult(Activity.RESULT_OK)
            finish()
        })

        buttonCrearGrupoChatCancelar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })




    }
}
