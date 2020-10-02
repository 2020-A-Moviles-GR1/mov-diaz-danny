package com.example.projectappmobile.module_game_room.presenter

import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.models.ConstantParameters
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.module_game_room.contracts.ConnectRoomContracts
import com.example.projectappmobile.module_game_room.interactor.ConnectRoomInteractor
import org.json.JSONObject

class ConnectRoomPresenter (
    private val context: Context,
    private val view: ConnectRoomContracts.View,
    private val handler: Handler
): ConnectRoomContracts.Presenter{

    private val interactor: ConnectRoomContracts.Interactor

    companion object {
        val TAG = "ConnectRoomPresenter"
    }

    init {

        interactor =
            ConnectRoomInteractor(
                context,
                this,
                handler
            )


    }


    override fun onResume() {
        view.showProgressBar()
        interactor.onResume()
    }

    override fun onPause() {
        interactor.onPause()
    }

    override fun onSendRoomNumber(room_number: Int) {
        view.showProgressBar()
        view.disableButtons()
        interactor.validateRoomNumber(room_number)
    }

    override fun onSendRoomNumberSuccess(room_number: Int) {
        view.hideProgressBar()
        view.showGameRoom(room_number)
    }

    override fun onDestroy() {
        interactor.onDestroy()
    }

    override fun onStreamPrediction(predictFunction: Int, room: Int) {
        view.showProgressBar()

        interactor.sendPredictionToWeb(predictFunction, room)
    }

    override fun onShowGeneralShortMessage(msg: String){
        view.hideProgressBar()
        GlobalMethods.showShortToast(context, msg)
    }

    override fun onShowGeneralLongMessage(msg: String){
        view.hideProgressBar()
        GlobalMethods.showLongToast(context, msg)
    }

    override fun onOpen() {
        view.hideProgressBar()
        Log.i(TAG, "Socket conectado correctamente en ${Parameters.WS_SERVER_PATH}")
    }

    override fun onMessage(jsonObject: JSONObject) {
        val id_message: Int = jsonObject.get("id_message") as Int
        val metadata: Int = jsonObject.get("metadata").toString().toInt()
        val server_message: String = jsonObject.get("server_message").toString()
        view.hideProgressBar()
        if(id_message == 1){

            if(metadata == ConstantParameters.SOCKET_SERVER_INSTRUCTION_VALIDATE_ROOM_NUMBER){
                Log.i(TAG, jsonObject.getJSONArray("data").toString())
                val room_data: Int = jsonObject.getJSONArray("data")[0].toString().toInt()
                onSendRoomNumberSuccess(room_data)
            }
            else if(metadata == ConstantParameters.SOCKET_SERVER_INSTRUCTION_TO_WEB){
                Log.i(TAG, server_message)
            }else{
                Log.w(TAG, "No se reconoce el origen de la petición inicial de estem ensaje")
            }
        }
        else{
            onShowGeneralLongMessage(server_message)

            if(metadata == ConstantParameters.SOCKET_SERVER_INSTRUCTION_VALIDATE_ROOM_NUMBER){
                view.enableButtons()
            }
            else{
                Log.e(TAG, server_message)
            }

        }
    }

    override fun onFailure(t: Throwable) {
        view.hideProgressBar()
        view.onSocketFailure(ErrorHandler.euphemiseMessage(t))
    }
}