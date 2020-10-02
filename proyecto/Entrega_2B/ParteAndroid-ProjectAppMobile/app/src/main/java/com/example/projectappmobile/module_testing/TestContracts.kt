package com.example.projectappmobile.module_testing

import android.widget.TextView
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.neural_network.NeuralNetwork
import com.example.projectappmobile.models.sockets.SocketListener

interface TestContracts {

    interface View {

        fun showShortToast(msg: String)
        fun showProgressBar()
        fun hideProgressBar()


        fun populateSpinner(functions: List<Function>?)

        fun enableButtonsNN()
        fun disableButtonsNN()
        fun enableButtonsRecordingForFunction()
        fun disableButtonsRecordingForFunction()
        fun hideToggleButtonStream()
        fun showToggleButtonStream()
        fun enableButtonRecord()
        fun enableButtonResetFunction()
        fun updateTextViewResult(text: String)
        fun clearLineChart(preloadAccelerometer: Boolean, preloadGyroscope:Boolean)
        fun plotDataAccelerometer(
            accelerometerX: Float,
            accelerometerY: Float,
            accelerometerZ: Float
        )

        fun plotDataGyroscope(gyroscopeX: Float, gyroscopeY: Float, gyroscopeZ: Float)
        fun showLongToast(output: String)
        fun updateTextViewLogNN(log: String)
        fun enableButtonUpload()
        fun enableButtonSaveModel()
    }

    interface  Presenter : SocketListener.Companion.SocketInterface, NeuralNetwork.NeuralNetworkInterface {
        fun onCreateGesture(id_function: Int, username_user:String, name:String,
                            data_accelerometer_x: String, data_accelerometer_y: String, data_accelerometer_z: String,
                            data_gyroscope_x: String, data_gyroscope_y: String, data_gyroscope_z: String,
                            is_augmented_data: Boolean = false)

        fun onCreateGestureFailed(msg: String)


        fun onCreateSamplesForGesture(
            id_function: Int, username_user: String,
            data_accelerometer_x: String, data_accelerometer_y: String, data_accelerometer_z: String,
            data_gyroscope_x: String, data_gyroscope_y: String, data_gyroscope_z: String,
            is_augmented_data: Boolean = false
        )


        fun onCreateSamplesForGestureSuccessful()

        fun onCreateSamplesForGestureFailed(msg: String)


        fun onPopulateSpinner()
        fun onGetFunctionsSuccessful(functions: List<Function>?)
        fun onGetFunctionsFailed(msg: String)


        fun onRecordClick(checkPlotAccelerometer: Boolean, checkPlotGyroscope: Boolean)
        fun onRecordingComplete()

        fun onUpdateTextViewResult(text: String)
        fun onClearLineChart(preloadAccelerometer: Boolean, preloadGyroscope:Boolean)
        fun onGetDataAccelerometer(
            accelerometerX: Float,
            accelerometerY: Float,
            accelerometerZ: Float,
            plotDataAccelerometer: Boolean
        )
        fun onGetDataGyroscope(
            gyroscopeX: Float,
            gyroscopeY: Float,
            gyroscopeZ: Float,
            plotDataGyroscope: Boolean
        )

        fun onResume()
        fun onPause()
        fun onDestruct()
        fun onStop()
        fun onUploadSample(
            username_user: String,
            id_function: Int,
            filename: String
        )

        fun onResetLocalDataForFunction(username_user: String, id_function: Int)
        fun onSavedLocally(msg: String)
        fun onResetLocalDataForFunctionSuccess(msg: String)
        fun onResetLocalDataForFunctionFailed(msg: String)
        fun onResumeDataPressed(
            username_user: String,
            mapFuntionToName: HashMap<Int, Any>
        )

        fun onGetResumeDataLocalSuccess(output: String)
        fun onGetResumeDataLocalFailed(msg: String)
        fun onStreamBegin(
            checkedPlotAccelerometer: Boolean,
            checkedPlotGyroscope: Boolean
        )
        fun onStreamStop()
        fun onTrainModelPressed(
            usernameUser: String,
            mapFuntionToName: java.util.HashMap<Int, Any>,
            config: NeuralNetwork.Config,
            textViewTestLogNN: TextView
        )
        fun onTrainModelFailed(msg: String)
        fun onUpdateTextViewLogNN(log: String)
        fun onTrainedModel(msg: String)
        fun onPredictOne()
        fun onPredictFailed(msg: String)
        fun onPredictSuccess(msg: String)
        fun onRecoverNNFailed(msg: String)
        fun onInfoNNPressed()
        fun onSaveModel()
        fun onSaveModeSuccess(msg: String)
        fun onSaveModelFailed(msg: String)
        fun onResetAll(usernameUser: String)
        fun onResetAllSuccess(msg: String)


    }

    interface Interactor {
        fun createGesture(id_function: Int, username_user:String, name:String,
                          data_accelerometer_x: String, data_accelerometer_y: String, data_accelerometer_z: String,
                          data_gyroscope_x: String, data_gyroscope_y: String, data_gyroscope_z: String,
                          is_augmented_data: Boolean = false)

        fun createSamplesForGesture(
            id_function: Int, username_user: String,
            data_accelerometer_x: String, data_accelerometer_y: String, data_accelerometer_z: String,
            data_gyroscope_x: String, data_gyroscope_y: String, data_gyroscope_z: String,
            is_augmented_data: Boolean = false
        )

        fun getFunctions()
        fun startRecordingData(checkPlotAccelerometer: Boolean, checkPlotGyroscope: Boolean)
        fun onResume()
        fun onPause()
        fun onDestruct()
        fun onStop()
        fun saveSampleLocalAndInDB(
            filename: String,
            username_user: String,
            id_function: Int
        )

        fun resetLocalDataForFunction(username_user: String, id_function: Int)
        fun getResumeDataLocal(
            username_user: String,
            mapFuntionToName: HashMap<Int, Any>
        )

        fun streamBegin(checkedPlotAccelerometer: Boolean, checkedPlotGyroscope: Boolean)
        fun streamStop()
        fun trainModel(
            usernameUser: String,
            mapFuntionToName: java.util.HashMap<Int, Any>,
            config: NeuralNetwork.Config,
            textViewTestLogNN: TextView
        )

        fun predictOne()
        fun getInfoNN(): String
        fun saveModel()
        fun resetAll(username_user: String)



    }
}