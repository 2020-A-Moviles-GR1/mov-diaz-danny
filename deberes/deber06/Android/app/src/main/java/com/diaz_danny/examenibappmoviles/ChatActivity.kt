package com.diaz_danny.examenibappmoviles

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.diaz_danny.examenibappmoviles.models.Parameters
import com.example.projectappmobile.models.retrofit.ApiClient
import com.example.projectappmobile.models.retrofit.ApiInterface
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataString
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpPost
import kotlinx.android.synthetic.main.activity_chat.*
import models.ChatGroup
import models.Message
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {

    private var url_image: String? = null
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


        buttonChatUploadImage.setOnClickListener {
            seleccionarImagen()
        }

        buttonChatCrear.setOnClickListener {

            if(url_image != null){
                val sender = spinnerChatIntegrante.selectedItem.toString()
                val message_content = editTextChatMensaje.text.toString()
                val url_web = editTextChatUrlWeb.text.toString()

                if(message_content == ""){
                    Toast.makeText(baseContext, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener;
                }

                if(url_web == ""){
                    Toast.makeText(baseContext, "El url de la web no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener;
                }


                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val today = sdf.format(Date())

                val parametrosMessage = listOf(
                    "sender" to sender,
                    "message_id" to "${Message.generateNewId()}",
                    "content" to message_content,
                    "date_creation" to today,
                    "modified" to "false",
                    "ping_registered" to "${Math.random()}",
                    "chatGroup" to ChatGroup.companion_chat_groups.get(grupoChatPosition).id.toString(),
                    "latitud" to getRandomLatitud(),
                    "longitud" to getRandomLongitud(),
                    "url_web" to url_web,
                    "url_image" to url_image
                )

                crearMessage(parametrosMessage)
            }else{
                Toast.makeText(this, "Elija o espere que se suba la imagen primero", Toast.LENGTH_SHORT).show()
            }


        }

        buttonChatRegresar.setOnClickListener({
            setResult(Activity.RESULT_CANCELED)
            finish()
        })

        buttonChatMap.setOnClickListener {
            val intent: Intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("grupoChatPosition", grupoChatPosition)
            startActivity(intent)
        }




    }

    private fun seleccionarImagen(){
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, 2)
    }



    private fun getRandomLatitud(): Double{
        val direction = if ((Math.random() * 2) -1 >= 0 ) 1 else -1;

        return -0.107120 + (((Math.random()*5)/10000) * direction);
    }

    private fun getRandomLongitud(): Double{
        val direction = if ((Math.random() * 2) -1 >= 0 ) 1 else -1;

        return -78.449212 + (((Math.random()*5)/10000) * direction);
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


    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            RESULT_OK -> {

                when(requestCode){
                    REQUEST_CODE_ACTUALIZAR_MENSAJE -> {
                        actualizarListView()
                    }
                    2 -> {
                        val selectedImage = data!!.data

                        val filePath: String = getPath(selectedImage)
                        val file_extn =
                            filePath.substring(filePath.lastIndexOf(".") + 1)

                        Log.i(TAG, filePath)


                        try {
                            if (file_extn == "img" || file_extn == "jpg" || file_extn == "jpeg" || file_extn == "gif" || file_extn == "png") {
                                Toast.makeText(this, filePath, Toast.LENGTH_LONG).show()

                                val file = File(filePath)

                                // val url_post_img = Parameters.urlPrincipal + "/file/upload"

                                val image: MultipartBody.Part =
                                    MultipartBody.Part.createFormData(
                                        "file",
                                        file.name,
                                        RequestBody.create(MediaType.parse("image/*"), file)
                                    )


                                val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
                                    ApiInterface::class.java
                                )

                                Log.d(TAG, image.toString())

                                val call: Call<MyServerResponseDataString>? = apiInterface?.uploadImage(
                                    image
                                )
                                call?.enqueue(object : Callback<MyServerResponseDataString?> {
                                    override fun onResponse(
                                        call: Call<MyServerResponseDataString?>,
                                        responseDataString: Response<MyServerResponseDataString?>
                                    ) {
                                        Log.d(TAG, "onResponse: : " + responseDataString.body())

                                        if(responseDataString.body() != null){

                                            url_image = responseDataString.body()!!.metadata;




                                        }

                                    }

                                    override fun onFailure(call: Call<MyServerResponseDataString?>, t: Throwable) {

                                        Log.e(TAG, "$t")
                                    }
                                })



                            } else {
                                Toast.makeText(this, "Formato no permitido", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                    }

                }
            }
            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "Actividad cancelada")
            }
        }
    }

    fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaColumns.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
            .getColumnIndexOrThrow(MediaColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


    private fun actualizarListView() {
        adaptador.notifyDataSetChanged()
    }


    private fun crearMessage(parametrosMessage: List<Pair<String, Any?>>) {

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
