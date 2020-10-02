package com.example.projectappmobile.module_gestures.contracts

import com.example.projectappmobile.GlobalContracts
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.neural_network.NeuralNetwork
import java.util.*

interface AddGestureContracts {

    interface View: GlobalContracts.View {

        fun disableButtons()
        fun enableButtons()

        fun updateTextViewMessage(text: String)

        fun populateSpinner(functions: List<Function>)

        fun onRamSavedSample()
        fun disableButtonSaveSample()
        fun back()
    }

    interface  Presenter: GlobalContracts.Presenter, NeuralNetwork.NeuralNetworkInterface {
        fun onPopulateSpinnerFunctions()
        fun onGetFunctionsSuccess(functions: List<Function>)
        fun onGetFunctionsFailed(msg: String)

        fun onSaveSample(
            queueAccX: LinkedList<Double>, queueAccY: LinkedList<Double>, queueAccZ: LinkedList<Double>,
            queueGyrX: LinkedList<Double>, queueGyrY: LinkedList<Double>, queueGyrZ: LinkedList<Double>
        )
        fun onSaveLocalSampleFinished()

        fun onCommitSaveGesture(id_function: Int, username_user: String, name: String)

        fun onCommitDBSuccess()
        fun onCommitDBFailed(msg: String)

        fun onCommitLocallyFinished()


        fun onUpdateTextViewMessage(text: String)
        fun onTrainedModel()

    }

    interface Interactor: GlobalContracts.Interactor {

        fun getFunctions()

        fun saveLocalSample(
            queueAccX: LinkedList<Double>, queueAccY: LinkedList<Double>, queueAccZ: LinkedList<Double>,
            queueGyrX: LinkedList<Double>, queueGyrY: LinkedList<Double>, queueGyrZ: LinkedList<Double>
        )

        fun commitGestureInDB(id_function: Int, username_user: String, name: String)
        fun commitGestureLocally(id_function: Int, username_user: String, name: String)
        fun trainModel()

    }
}