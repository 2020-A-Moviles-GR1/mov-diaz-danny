package com.diaz_danny.examenibappmoviles

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_actualizar_mensaje.*

class ActualizarMensajeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_mensaje)

        val grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)
        val mensajePosition = intent.getIntExtra("mensajePosition", -1)

        //llenar campos

        buttonActualizarMensajeActualizar.setOnClickListener({
            //todo código para actualizar mensaje
            setResult(Activity.RESULT_OK)
            finish()
        })

        buttonActualizarMensajeCancelar.setOnClickListener({

            setResult(Activity.RESULT_CANCELED)
            finish()
        })


    }
}
