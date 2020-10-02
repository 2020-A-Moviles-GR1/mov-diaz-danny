package com.example.projectappmobile.module_gestures.presenter

import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.R
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.module_gestures.contracts.AddGestureContracts
import com.example.projectappmobile.module_gestures.contracts.UpdateGestureContracts
import com.example.projectappmobile.module_gestures.interactor.AddGestureInteractor
import com.example.projectappmobile.module_gestures.interactor.UpdateGestureInteractor
import com.example.projectappmobile.module_testing.TestPresenter
import java.util.*

class UpdateGesturePresenter (
    private val context: Context,
    private val view: UpdateGestureContracts.View,
    private val handler: Handler
): UpdateGestureContracts.Presenter{

    private val interactor: UpdateGestureContracts.Interactor

    init {
        interactor = UpdateGestureInteractor(context, this, handler)
    }



    override fun onSaveSample(
        queueAccX: LinkedList<Double>,
        queueAccY: LinkedList<Double>,
        queueAccZ: LinkedList<Double>,
        queueGyrX: LinkedList<Double>,
        queueGyrY: LinkedList<Double>,
        queueGyrZ: LinkedList<Double>
    ) {
        view.disableButtons()
        interactor.saveLocalSample(queueAccX, queueAccY, queueAccZ, queueGyrX, queueGyrY, queueGyrZ)
    }

    override fun onSaveLocalSampleFinished() {
        view.enableButtons()
        view.disableButtonSaveSample()
        view.onRamSavedSample()
        onShowGeneralShortMessage(context.getString(R.string.msg_saved))
    }

    override fun onCommitSaveGesture(id_function: Int, username_user: String, name: String) {
        view.showProgressBar()
        view.disableButtons()
        interactor.commitGestureInDB(id_function, username_user, name)
        interactor.commitGestureLocally(id_function, username_user, name)
    }

    override fun onCommitDBSuccess() {
        view.enableButtons()
        view.disableButtonSaveSample()

    }

    override fun onCommitDBFailed(msg: String) {
        view.enableButtons()
        view.disableButtonSaveSample()

    }

    override fun onCommitLocallyFinished() {
        //todo: verificar si se puede hacer procesamiento en la nube o no
        view.showProgressBar() // el que entrena tiene potestad en show progress bar
        interactor.trainModel()
    }

    override fun onUpdateTextViewMessage(text: String) {
        view.updateTextViewMessage(text)
    }

    override fun onTrainedModel() {
        view.hideProgressBar()
        view.back()
        onShowGeneralLongMessage("Modelo entrenado")
    }

    override fun onResume() {
        interactor.onResume()
    }

    override fun onPause() {
        interactor.onPause()
    }

    override fun onShowGeneralShortMessage(msg: String){
        view.hideProgressBar()
        GlobalMethods.showShortToast(context, msg)
    }

    override fun onShowGeneralLongMessage(msg: String){
        view.hideProgressBar()
        GlobalMethods.showLongToast(context, msg)
    }

    companion object {
        val TAG = "AddGesturePresenter"
    }

    override fun onEpochBegin(epoch: Int, msg: String) {
        Log.i(TAG, msg)
    }

    override fun onEpochPartFinished(actual_m: Int, msg: String) {
        Log.i(TAG, msg)
    }

    override fun onUpdateCompleteLog(log: String) {
        // viene de un hilo, de modo que se actualiza las vistas en el hilo principal
        handler.post{
            view.updateTextViewMessage(log)
        }
    }


}