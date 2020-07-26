package com.diaz_danny.examenibappmoviles

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_chat.*
import models.ChatGroup
import models.Message

class ChatActivity : AppCompatActivity() {

    private val REQUEST_CODE_ACTUALIZAR_MENSAJE: Int = 201
    val TAG = "ChatActivity"

    lateinit var adaptador: ArrayAdapter<Message>

    var grupoChatPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)

        if (grupoChatPosition == -1){
            Toast.makeText(this, "Ha ocurrido un error al enviar la posicion", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        adaptador = ArrayAdapter(
            this,  // contexto
            android.R.layout.simple_list_item_1,  // nombre de layout
            ChatGroup.companion_chat_groups.get(grupoChatPosition).messages  // lista
        )

        configureListView()

        buttonChatCrear.setOnClickListener({
           // todo: crear

            actualizarListView()
        })




    }

    private fun configureListView() {
        listViewChatMensajes.adapter = adaptador

        listViewChatMensajes.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->
            Log.i(TAG, "Posicion presionada $position")

            val intentDetalles = Intent(this, DetallesActivity::class.java)
            intentDetalles.putExtra("informacion", ChatGroup.companion_chat_groups.get(grupoChatPosition).messages.get(position).read())
            startActivity(intentDetalles)

        }

        listViewChatMensajes.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val opciones = arrayOf("Actualizar", "Eliminar")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Elija una opción")
            builder.setItems(opciones, DialogInterface.OnClickListener { dialog, which ->

                when(which){
                    0 -> {

                        val intentActualizarMensaje = Intent(this, ActualizarGrupoChatActivity::class.java)
                        intentActualizarMensaje.putExtra("grupoChatPosition", grupoChatPosition)
                        intentActualizarMensaje.putExtra("mensajePosition", position)
                        startActivityForResult(intentActualizarMensaje, REQUEST_CODE_ACTUALIZAR_MENSAJE)

                    }

                    1 -> {
                        // todo: eliminar
                        actualizarListView()
                    }

                }
                // the user clicked on colors[which]
            })
            builder.show()

            return@OnItemLongClickListener true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            RESULT_OK -> {

                when(requestCode){
                    REQUEST_CODE_ACTUALIZAR_MENSAJE -> {
                        actualizarListView()
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "Actividad cancelada")
            }
        }
    }


    private fun actualizarListView() {
        adaptador.notifyDataSetChanged()
    }
}
