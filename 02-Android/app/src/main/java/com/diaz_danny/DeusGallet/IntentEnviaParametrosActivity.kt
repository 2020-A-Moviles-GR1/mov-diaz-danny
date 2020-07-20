package com.diaz_danny.DeusGallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_intent_envia_parametros.*

class IntentEnviaParametrosActivity : AppCompatActivity() {

    val TAG = "IntentEnviaParametrosActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent_envia_parametros)

        val numeroEncontrado = intent.getIntExtra("numero", -1)
        Log.i(TAG, "El numero encontrado es: ${numeroEncontrado}")

        val textoCompartido: String? = intent.getStringExtra(Intent.EXTRA_TEXT)

        if(textoCompartido != null){
            Log.i(TAG, "El texto es: ${textoCompartido}")
        }

        buttonDevolverRespuesta.setOnClickListener(
            {
                finish()
            }

        )
    }
}
