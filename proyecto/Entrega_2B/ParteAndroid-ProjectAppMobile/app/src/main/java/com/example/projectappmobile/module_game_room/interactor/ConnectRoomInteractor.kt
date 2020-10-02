package com.example.projectappmobile.module_game_room.interactor

import android.content.Context
import android.os.Handler
import com.example.projectappmobile.R
import com.example.projectappmobile.models.ConstantParameters
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.models.sockets.SocketListener
import com.example.projectappmobile.module_game_room.contracts.ConnectRoomContracts
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONException
import org.json.JSONObject

class ConnectRoomInteractor  (
    private val context: Context,
    private val callBackPresenter: ConnectRoomContracts.Presenter,
    private val handler: Handler
): ConnectRoomContracts.Interactor{

    lateinit var webSocket: WebSocket

    private val user: User
    init {
        user = User.getLoggedIngUser()!!
        val client = OkHttpClient()
        // opcional .addHeader("sec-websocket-protocol", "echo-protocol")
        val request: Request = Request.Builder().url(Parameters.WS_SERVER_PATH).addHeader("sec-websocket-protocol", "echo-protocol").build()

        webSocket = client.newWebSocket(request, SocketListener(
            context, handler, callBackPresenter)
        )
    }

    override fun onDestroy() {
        //todo: validar que al ir al free mode no se pierde la conexión
        webSocket.close(1001, "Actividad destruida")
    }

    override fun sendPredictionToWeb(predictFunction: Int, room: Int) {
        val jsonObject = JSONObject()
        val data = "${User.getLoggedIngUser()?.username}#$predictFunction#$room"
        try {
            jsonObject.put("id", ConstantParameters.SOCKET_SERVER_INSTRUCTION_TO_WEB)
            jsonObject.put("data",  data)
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
        } catch (e: JSONException) {
            println("Error $e")
            callBackPresenter.onShowGeneralShortMessage(context.getString(R.string.error_socket_send_message))
        }
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun validateRoomNumber(room_number: Int) {
        socketSendMessageForValidateRoom(
            "${user.username}${ConstantParameters.SEPARATOR}$room_number"
        )
    }

    private fun socketSendMessageForValidateRoom(data: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("id", ConstantParameters.SOCKET_SERVER_INSTRUCTION_VALIDATE_ROOM_NUMBER)
            jsonObject.put("data", data)
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
        } catch (e: JSONException) {
            println("Error $e")
            callBackPresenter.onShowGeneralShortMessage(context.getString(R.string.error_socket_send_message))
        }
    }


}