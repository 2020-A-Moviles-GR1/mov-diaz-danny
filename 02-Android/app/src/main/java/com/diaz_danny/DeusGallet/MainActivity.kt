package com.diaz_danny.DeusGallet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

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
            val danny = Usuario("Danny", 22, Date(), 1.0)
            val cachetes = Mascota("Cachetes", danny)
            val arreglo_mascotas = arrayListOf<Mascota>(cachetes, cachetes)

            intentExplicito.putExtra("cachetes", cachetes)
            intentExplicito.putExtra("arregloMascotas", arreglo_mascotas)
            startActivity(intentExplicito)
        }

        buttonIntentImplicito.setOnClickListener{
            val intentConRespuesta = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intentConRespuesta, 304)
        }

        buttonRespuestaPropia.setOnClickListener{
            val intentExplicito = Intent(this, IntentEnviaParametrosActivity::class.java)

            startActivityForResult(intentExplicito, 305)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            RESULT_OK -> {
                Log.i(TAG, "OK ")

                when(requestCode){
                    304 -> {
                        val uri = data?.data

                        if(uri != null){
                            val cursor = contentResolver.query(
                                uri, null, null, null, null, null
                            )

                            cursor?.moveToFirst()
                            val indiceTelefono = cursor?.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )

                            val telefono = cursor?.getString(indiceTelefono!!)
                            cursor?.close()
                            Log.i(TAG, "Telefono ${telefono}")
                        }
                    }

                    305 -> {

                        if( data != null){
                            val nombre = data.getStringExtra("nombre")
                            val edad = data.getIntExtra("edad", -1)
                            Log.i(TAG, "Nombre: ${nombre}, Edad: ${edad}")

                        }

                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "=(")
            }
        }
    }



}
