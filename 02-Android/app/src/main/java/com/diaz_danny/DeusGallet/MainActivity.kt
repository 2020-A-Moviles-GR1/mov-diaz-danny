package com.diaz_danny.DeusGallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonCicloVida.setOnClickListener{
            startActivity(Intent(applicationContext, CicloVidaActivity::class.java));
        }


        buttonListView.setOnClickListener{
            startActivity(Intent(applicationContext, ListViewActivity::class.java));
        }

        buttonIntentRespuesta.setOnClickListener{
            val intentExplicito = Intent(this, IntentEnviaParametrosActivity::class.java)
            intentExplicito.putExtra("numero", 2)
            startActivity(intentExplicito)
        }

    }



}
