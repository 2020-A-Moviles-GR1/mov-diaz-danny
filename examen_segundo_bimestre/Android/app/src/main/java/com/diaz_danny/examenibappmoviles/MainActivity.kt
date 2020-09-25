package com.diaz_danny.examenibappmoviles

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.beust.klaxon.Klaxon
import com.diaz_danny.examenibappmoviles.models.Parameters.Companion.urlPrincipal
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import kotlinx.android.synthetic.main.activity_main.*
import models.ChatGroup


class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private val REQUEST_CODE_CREAR_GRUPO_CHAT: Int = 102
    private val REQUEST_CODE_CONTRASENIA: Int = 99
    private val REQUEST_CODE_ACTUALIZAR_GRUPO_CHAT: Int = 101

    lateinit var adaptador: ArrayAdapter<ChatGroup>;


    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(Looper.getMainLooper())

        obtenerChatGroups();


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);

        }





        buttonMainAgregarGrupoChat.setOnClickListener({
            val intentCrearGrupoChat = Intent(this, CrearGrupoChatActivity::class.java)
            intentCrearGrupoChat.putExtra("id", ChatGroup.generateNewId())
            startActivityForResult(intentCrearGrupoChat, REQUEST_CODE_CREAR_GRUPO_CHAT)
        })





    }

    private fun obtenerChatGroups() {

        val url = urlPrincipal + "/chatgroup"

        url
            .httpGet()
            .responseString{
                    request, response, result ->

                when(result){
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        ChatGroup.companion_chat_groups = (Klaxon().parseArray<ChatGroup>(data) as ArrayList<ChatGroup>?)!!

                        Log.i(TAG, "Data: ${data}\nSize: ${ChatGroup.companion_chat_groups?.size}")

                        if(ChatGroup.companion_chat_groups != null){
                            adaptador = ArrayAdapter(
                                this, android.R.layout.simple_list_item_1, ChatGroup.companion_chat_groups!!
                            )
                            Log.i(TAG, "Adaptador: $adaptador")

                            handler.post{
                                configureListView()
                            }
                        }
                    }
                    is com.github.kittinunf.result.Result.Failure -> {
                        val ex = result.getException()
                        Log.i(TAG, "Error: ${ex.message}")
                    }
                }

            }

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

                        val deletedId = ChatGroup.companion_chat_groups?.get(position)?.id

                        val url = urlPrincipal + "/chatgroup/$deletedId"

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
                                        Log.i(TAG, "Deleted Group Chat: $data")

                                        ChatGroup.companion_chat_groups?.removeAt(position)
                                        handler.post{
                                            actualizarListView()
                                        }
                                    }
                                }

                            }


                    }

                    2 -> {
                        val intentDetalles = Intent(this, DetallesActivity::class.java)
                        intentDetalles.putExtra("informacion",
                            ChatGroup.companion_chat_groups?.get(position)?.read()
                        )
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
