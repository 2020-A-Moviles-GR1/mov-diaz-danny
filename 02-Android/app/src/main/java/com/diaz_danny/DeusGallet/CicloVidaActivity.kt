package com.diaz_danny.DeusGallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_ciclo_vida.*

class CicloVidaActivity : AppCompatActivity() {

    val TAG = "CicloVidaActivity"

    var numeroActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ciclo_vida)

        buttonAnadir.setOnClickListener{
            sumarUnValor()
        }

        Log.i(TAG, "OnCreate")
    }

    fun sumarUnValor(){
        numeroActual = numeroActual +1
        textViewNumero.text = numeroActual.toString()
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "OnStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.i(TAG, "onSaveInstanceState simple")
        outState.run {
            putInt("numeroActualGuardado", numeroActual)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i(TAG, "onRestoreInstanceState")
        val valorRecuperado = savedInstanceState.getInt("numeroActualGuardado")
        if(valorRecuperado != null){
            this.numeroActual = valorRecuperado
            textViewNumero.text = this.numeroActual.toString()
        }
    }

}
