package com.example.projectappmobile.module_game_room.contracts

import com.example.projectappmobile.GlobalContracts
import com.example.projectappmobile.models.sockets.SocketListener

interface ConnectRoomContracts {
    interface View: GlobalContracts.View {

        fun onDestroy()
        fun disableButtons()
        fun showGameRoom(room_number: Int)
        fun enableButtons()
        fun onSocketFailure(euphemiseMessage: String)
    }

    interface  Presenter: GlobalContracts.Presenter, SocketListener.Companion.SocketInterface {
        fun onSendRoomNumber(room_number: Int)
        fun onSendRoomNumberSuccess(room_number: Int)
        fun onDestroy()
        fun onStreamPrediction(predictFunction: Int, room: Int)
    }

    interface Interactor: GlobalContracts.Interactor {
        fun validateRoomNumber(room_number: Int)
        fun onDestroy()
        fun sendPredictionToWeb(predictFunction: Int, room: Int)
    }
}