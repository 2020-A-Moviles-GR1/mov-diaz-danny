package com.diaz_danny.examenibappmoviles

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_crear_grupo_chat.*
import java.time.LocalDateTime

class CrearGrupoChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_grupo_chat)


        val id_generada = intent.getIntExtra("id", -1)
        if(id_generada == -1){
            finish()
            return
        }
        val today = "2020-07-12 "// LocalDateTime.now().toString()
        textViewCrearGrupoChatId.text = id_generada.toString()
        textViewCrearGrupoChatFecha.text = today



        buttonCrearGrupoChatCrear.setOnClickListener({
            //todo: codigo de creación
            setResult(Activity.RESULT_OK)
        })

        buttonCrearGrupoChatCancelar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })




    }
}
