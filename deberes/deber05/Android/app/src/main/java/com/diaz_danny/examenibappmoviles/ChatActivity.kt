package com.diaz_danny.examenibappmoviles

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.beust.klaxon.Klaxon
import com.diaz_danny.examenibappmoviles.models.Parameters
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import kotlinx.android.synthetic.main.activity_chat.*
import models.ChatGroup
import models.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val REQUEST_CODE_ACTUALIZAR_MENSAJE: Int = 201
    val TAG = "ChatActivity"

    lateinit var adaptador: ArrayAdapter<Message>

    var grupoChatPosition: Int = -1
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        handler = Handler(Looper.getMainLooper())

        grupoChatPosition = intent.getIntExtra("grupoChatPosition", -1)

        if (grupoChatPosition == -1){
            Toast.makeText(this, "Ha ocurrido un error al enviar la posicion", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        adaptador = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.let {
            ArrayAdapter(
                this,  // contexto
                android.R.layout.simple_list_item_1,  // nombre de layout
                it  // lista
            )
        }!!

        configureListView()

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val today = sdf.format(Date())

        buttonChatCrear.setOnClickListener {

            val sender = spinnerChatIntegrante.selectedItem.toString()
            val message_content = editTextChatMensaje.text.toString()

            if(message_content == ""){
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            val parametrosMessage = listOf(
                "sender" to sender,
                "message_id" to "${Message.generateNewId()}",
                "content" to message_content,
                "date_creation" to today,
                "modified" to "false",
                "ping_registered" to "${Math.random()}",
                "chatGroup" to ChatGroup.companion_chat_groups.get(grupoChatPosition).id.toString()
            )

            crearMessage(parametrosMessage)

        }

        buttonChatRegresar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })




    }

    private fun configureListView() {
        listViewChatMensajes.adapter = adaptador

        listViewChatMensajes.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->
            Log.i(TAG, "Posicion presionada $position")

            val intentDetalles = Intent(this, DetallesActivity::class.java)
            intentDetalles.putExtra("informacion", ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(position)
                ?.read()
            )
            startActivity(intentDetalles)

        }

        listViewChatMensajes.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val opciones = arrayOf("Actualizar", "Eliminar")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Elija una opción")
            builder.setItems(opciones, DialogInterface.OnClickListener { dialog, which ->

                when(which){
                    0 -> {

                        val intentActualizarMensaje = Intent(this, ActualizarMensajeActivity::class.java)
                        intentActualizarMensaje.putExtra("grupoChatPosition", grupoChatPosition)
                        intentActualizarMensaje.putExtra("mensajePosition", position)
                        startActivityForResult(intentActualizarMensaje, REQUEST_CODE_ACTUALIZAR_MENSAJE)

                    }

                    1 -> {

                        val deletedId = ChatGroup.companion_chat_groups?.get(grupoChatPosition)?.messages?.get(position)?.id

                        val url = Parameters.urlPrincipal + "/message/$deletedId"

                        url
                            .httpDelete()
                            .responseString {
                                    req, res, result ->
                                when(result){
                                    is com.github.kittinunf.result.Result.Failure -> {
                                        val error = result.getException()
                                        Log.i(TAG, "Error: $error")
                                    }
                                    is com.github.kittinunf.result.Result.Success -> {
                                        val data = result.get()
                                        Log.i(TAG, "Deleted Message: $data")

                                        ChatGroup.companion_chat_groups.get(grupoChatPosition)?.messages?.removeAt(position)
                                        handler.post{
                                            actualizarListView()
                                        }
                                    }
                                }

                            }

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


    private fun crearMessage(parametrosMessage: List<Pair<String, String>>) {

        val url = Parameters.urlPrincipal + "/message"

        url
            .httpPost(parametrosMessage)
            .responseString {
                    req, res, result ->
                when(result){
                    is com.github.kittinunf.result.Result.Failure -> {
                        val error = result.getException()
                        Log.i(TAG, "Error: $error")
                    }
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        Log.i(TAG, "New Message in group: $data")

                        val message: Message? = Klaxon().parse<Message>(data)

                        if (message != null) {
                            ChatGroup.companion_chat_groups.get(grupoChatPosition).messages?.add(
                                message
                            )
                            handler.post{
                                actualizarListView()
                                editTextChatMensaje.setText("")
                            }
                        }else{
                            Toast.makeText(this, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show()
                        }




                    }
                }

            }
    }
}
