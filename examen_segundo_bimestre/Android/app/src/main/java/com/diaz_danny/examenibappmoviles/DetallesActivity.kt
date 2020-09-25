package com.diaz_danny.examenibappmoviles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detalles.*

class DetallesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        val informacion = intent.getStringExtra("informacion")
        textViewDetalles.text = informacion

        buttonDetallesAceptar.setOnClickListener({
            finish()
        })


    }
}
