package com.example.projectappmobile.module_testing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.example.projectappmobile.models.database.Function
import android.os.Handler
import android.util.Log
import android.widget.TextView
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.neural_network.NeuralNetwork
import com.example.projectappmobile.models.sockets.SocketListener
import org.json.JSONObject

class TestPresenter(
    mContext: Context,
    val view: TestContracts.View,
    handler: Handler,
    sensorManager: SensorManager,
    linearAccelerometer: Sensor,
    gyroscope: Sensor,
    username_user: String
) : TestContracts.Presenter {

    val interactor: TestContracts.Interactor
    init {
        interactor = TestInteractor(mContext, this, handler,
            sensorManager, linearAccelerometer, gyroscope, username_user)
    }

    override fun onCreateGesture(
        id_function: Int,
        username_user: String,
        name: String,
        data_accelerometer_x: String,
        data_accelerometer_y: String,
        data_accelerometer_z: String,
        data_gyroscope_x: String,
        data_gyroscope_y: String,
        data_gyroscope_z: String,
        is_augmented_data: Boolean
    ) {
        view.showProgressBar()
        interactor.createGesture(id_function, username_user, name, data_accelerometer_x, data_accelerometer_y,
            data_accelerometer_z, data_gyroscope_x, data_gyroscope_y, data_gyroscope_z, is_augmented_data)
    }

    override fun onCreateGestureFailed(msg: String) {
        view.hideProgressBar()
        view.showShortToast(msg)
        view.enableButtonRecord()
    }

    override fun onCreateSamplesForGesture(
        id_function: Int,
        username_user: String,
        data_accelerometer_x: String,
        data_accelerometer_y: String,
        data_accelerometer_z: String,
        data_gyroscope_x: String,
        data_gyroscope_y: String,
        data_gyroscope_z: String,
        is_augmented_data: Boolean
    ) {
        view.showProgressBar()
        interactor.createSamplesForGesture(
            id_function, username_user,
            data_accelerometer_x, data_accelerometer_y, data_accelerometer_z,
            data_gyroscope_x, data_gyroscope_y, data_gyroscope_z,
            is_augmented_data
        )

    }

    override fun onCreateSamplesForGestureSuccessful() {
        view.hideProgressBar()
        view.enableButtonRecord()
        view.enableButtonResetFunction()
    }

    override fun onCreateSamplesForGestureFailed(msg: String) {
        view.hideProgressBar()
        view.showShortToast(msg)
        view.enableButtonsNN()
    }

    override fun onPopulateSpinner() {
        view.showProgressBar()
        interactor.getFunctions()

    }

    override fun onGetFunctionsSuccessful(functions: List<Function>?) {
        view.hideProgressBar()
        view.populateSpinner(functions)

    }

    override fun onGetFunctionsFailed(msg: String) {
        view.hideProgressBar()
        view.showShortToast(msg)
    }

    override fun onRecordClick(checkPlotAccelerometer: Boolean, checkPlotGyroscope: Boolean) {
        view.disableButtonsRecordingForFunction()
        view.disableButtonsNN()
        view.hideToggleButtonStream()
        interactor.startRecordingData(checkPlotAccelerometer, checkPlotGyroscope)

    }


    override fun onRecordingComplete() {
        view.enableButtonsRecordingForFunction()
        view.enableButtonsNN()
        view.showToggleButtonStream()
    }

    override fun onUpdateTextViewResult(text: String) {
        view.updateTextViewResult(text)
    }

    override fun onClearLineChart(preloadAccelerometer: Boolean, preloadGyroscope:Boolean) {
        view.clearLineChart(preloadAccelerometer, preloadGyroscope)
    }

    override fun onGetDataAccelerometer(
        accelerometerX: Float,
        accelerometerY: Float,
        accelerometerZ: Float,
        plotDataAccelerometer: Boolean
    ) {
        if(plotDataAccelerometer)
            view.plotDataAccelerometer(accelerometerX, accelerometerY, accelerometerZ)
    }

    override fun onGetDataGyroscope(
        gyroscopeX: Float,
        gyroscopeY: Float,
        gyroscopeZ: Float,
        plotDataGyroscope: Boolean
    ) {
        if(plotDataGyroscope)
            view.plotDataGyroscope(gyroscopeX, gyroscopeY, gyroscopeZ)
    }

    override fun onResume() {
        interactor.onResume()
    }

    override fun onPause() {
        interactor.onPause()
    }

    override fun onDestruct() {
        interactor.onDestruct()
    }

    override fun onStop() {
        interactor.onStop()
    }

    override fun onUploadSample(
        username_user: String,
        id_function: Int,
        filename: String
    ) {
        view.disableButtonsRecordingForFunction()
        view.disableButtonsNN()
        interactor.saveSampleLocalAndInDB(filename, username_user, id_function)
    }

    override fun onResetLocalDataForFunction(username_user: String, id_function: Int) {
        interactor.resetLocalDataForFunction(username_user, id_function)
    }

    override fun onSavedLocally(msg: String) {
        view.showShortToast(msg)
    }

    override fun onResetLocalDataForFunctionSuccess(msg: String) {
        view.showShortToast(msg)
    }

    override fun onResetLocalDataForFunctionFailed(msg: String) {
        view.showShortToast(msg)
    }

    override fun onResumeDataPressed(
        username_user: String,
        mapFuntionToName: HashMap<Int, Any>
    ) {
        interactor.getResumeDataLocal(username_user, mapFuntionToName)
    }

    override fun onGetResumeDataLocalSuccess(output: String) {
        view.showLongToast(output)
    }

    override fun onGetResumeDataLocalFailed(msg: String) {
        view.showShortToast(msg)
    }

    override fun onStreamBegin(
        checkedPlotAccelerometer: Boolean,
        checkedPlotGyroscope: Boolean
    ) {
        view.disableButtonsNN()
        view.disableButtonsRecordingForFunction()
        interactor.streamBegin(checkedPlotAccelerometer, checkedPlotGyroscope)
    }

    override fun onStreamStop() {
        interactor.streamStop()
        view.enableButtonsNN()
        view.enableButtonsRecordingForFunction()

    }

    override fun onTrainModelPressed(
        usernameUser: String,
        mapFuntionToName: java.util.HashMap<Int, Any>,
        config: NeuralNetwork.Config,
        textViewTestLogNN: TextView
    ) {
        view.disableButtonsRecordingForFunction()
        view.disableButtonsNN()
        view.showProgressBar()
        Thread(Runnable {
            interactor.trainModel(usernameUser, mapFuntionToName, config, textViewTestLogNN)
        }).start()

    }

    override fun onTrainModelFailed(msg: String) {
        view.enableButtonsRecordingForFunction()
        view.enableButtonsNN()
        view.hideProgressBar()
    }

    override fun onUpdateTextViewLogNN(log: String) {
        view.updateTextViewLogNN(log)
    }

    override fun onTrainedModel(msg: String) {
        view.enableButtonsRecordingForFunction()
        view.enableButtonsNN()
        view.enableButtonSaveModel()
        view.hideProgressBar()
        view.enableButtonUpload()
    }

    override fun onPredictOne() {
        interactor.predictOne()
    }

    override fun onPredictFailed(msg: String) {
        view.showLongToast(msg)
    }

    override fun onPredictSuccess(msg: String) {
        view.showLongToast(msg)
    }

    override fun onRecoverNNFailed(msg: String) {
        view.showLongToast(msg)
    }

    override fun onInfoNNPressed() {
        view.showLongToast(interactor.getInfoNN())
    }

    override fun onSaveModel() {
        view.disableButtonsNN()
        view.showProgressBar()
        interactor.saveModel()
    }

    override fun onSaveModeSuccess(msg: String) {
        view.enableButtonsNN()
        view.hideProgressBar()
        view.showShortToast(msg)
    }

    override fun onSaveModelFailed(msg: String) {
        view.enableButtonsNN()
        view.hideProgressBar()
        view.showLongToast(msg)
    }

    override fun onResetAll(usernameUser: String) {
        interactor.resetAll(usernameUser)
    }

    override fun onResetAllSuccess(msg: String) {
        view.showLongToast(msg)
    }

    override fun onOpen() {
        view.showShortToast("Conectado al servidor")
    }

    override fun onMessage(jsonObject: JSONObject) {
        println(jsonObject)

    }

    override fun onFailure(t: Throwable) {
        Log.e(TAG, t.toString())
        view.showLongToast(ErrorHandler.euphemiseMessage(t))
    }

    companion object {
        val TAG = "TestPresenter"
    }

    override fun onEpochBegin(epoch: Int, msg: String) {
        Log.i(TAG, msg)
    }

    override fun onEpochPartFinished(actual_m: Int, msg: String) {
        Log.i(TAG, msg)
    }

    override fun onUpdateCompleteLog(log: String) {
        view.updateTextViewLogNN(log)
    }


}