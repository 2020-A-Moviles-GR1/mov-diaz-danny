package com.diaz_danny.examenibappmoviles

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import models.ChatGroup


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_CREAR_GRUPO_CHAT: Int = 102
    private val REQUEST_CODE_CONTRASENIA: Int = 99
    private val REQUEST_CODE_ACTUALIZAR_GRUPO_CHAT: Int = 101

    val adaptador = ArrayAdapter(
        this,  // contexto
        android.R.layout.simple_list_item_1,  // nombre de layout
        ChatGroup.companion_chat_groups  // lista
    )


    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureListView()

        buttonMainAgregarGrupoChat.setOnClickListener({
            val intentCrearGrupoChat = Intent(this, CrearGrupoChatActivity::class.java)
            intentCrearGrupoChat.putExtra("id", ChatGroup.generateNewId())
            startActivityForResult(intentCrearGrupoChat, REQUEST_CODE_CREAR_GRUPO_CHAT)
        })



    }

    private fun configureListView() {
        listViewMainGruposChat.adapter = adaptador

        listViewMainGruposChat.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->
            Log.i(TAG, "Posicion presionada $position")

            val intentContrasenia = Intent(this, ContraseniaActivity::class.java)
            intentContrasenia.putExtra("grupoChatPosition", position)
            startActivityForResult(intentContrasenia, REQUEST_CODE_CONTRASENIA)
        }

        listViewMainGruposChat.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val opciones = arrayOf("Actualizar", "Eliminar", "Detalles")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Elija una opción")
            builder.setItems(opciones, DialogInterface.OnClickListener { dialog, which ->

                when(which){
                    0 -> {
                        val intentActualizarGrupoChat = Intent(this, ActualizarGrupoChatActivity::class.java)
                        intentActualizarGrupoChat.putExtra("grupoChatPosition", position)
                        startActivityForResult(intentActualizarGrupoChat, REQUEST_CODE_ACTUALIZAR_GRUPO_CHAT)
                    }

                    1 -> {
                        // todo: eliminar
                        actualizarListView()
                    }

                    2 -> {
                        val intentDetalles = Intent(this, DetallesActivity::class.java)
                        intentDetalles.putExtra("informacion", ChatGroup.companion_chat_groups.get(position).read())
                        startActivity(intentDetalles)
                    }

                }
                // the user clicked on colors[which]
            })
            builder.show()

            return@OnItemLongClickListener true
        }
    }

    private fun actualizarListView() {
        adaptador.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            RESULT_OK -> {

                when(requestCode){
                    REQUEST_CODE_CREAR_GRUPO_CHAT -> {
                        actualizarListView()
                    }

                    REQUEST_CODE_CONTRASENIA -> {

                        val position = data?.getIntExtra("grupoChatPosition", -1)

                        val intentChat = Intent(this, ChatActivity::class.java)
                        intentChat.putExtra("grupoChatPosition", position)
                        startActivity(intentChat)
                    }

                    REQUEST_CODE_ACTUALIZAR_GRUPO_CHAT -> {
                        actualizarListView()
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "Actividad cancelada")
            }
        }
    }





}
