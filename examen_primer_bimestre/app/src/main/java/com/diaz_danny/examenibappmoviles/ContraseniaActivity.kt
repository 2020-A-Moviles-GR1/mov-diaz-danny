package com.diaz_danny.examenibappmoviles

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_contrasenia.*
import models.ChatGroup

class ContraseniaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrasenia)

        val grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)
        if(grupoChatPosition == -1){
            finish()
            return
        }

        buttonContraseniaAceptar.setOnClickListener({
            if(editTextContrasenia.text.toString() == ChatGroup.companion_chat_groups.get(grupoChatPosition).password){

                val intentRespuesta = Intent()
                intentRespuesta.putExtra("grupoChatPosition", grupoChatPosition)
                setResult(Activity.RESULT_OK, intentRespuesta)
                finish()
            }else{
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        })

        buttonContraseniaCancelar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })

    }
}
