package com.example.projectappmobile.module_gestures.view

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.R
import com.example.projectappmobile.models.MyLineChart
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.SensorData
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.module_gestures.NNConfigParametersActivity
import com.example.projectappmobile.module_gestures.contracts.UpdateGestureContracts
import com.example.projectappmobile.module_gestures.presenter.UpdateGesturePresenter
import kotlinx.android.synthetic.main.activity_update_gesture.*
import kotlinx.android.synthetic.main.activity_update_gesture.checkBoxShowAccelerometerChart
import kotlinx.android.synthetic.main.activity_update_gesture.checkBoxShowGyroscopeChart
import kotlinx.android.synthetic.main.activity_update_gesture.progressBar

class UpdateGestureActivity : AppCompatActivity(), SensorData.SensorDataInterface, UpdateGestureContracts.View {

    private lateinit var gyroscope: Sensor
    private lateinit var linearAccelerometer: Sensor
    private lateinit var sensorManager: SensorManager

    private lateinit var myLineChart: MyLineChart
    private lateinit var handler: Handler

    private var samples_saved: Byte = 0

    companion object {
        const val TAG: String = "UpdateGestureActivity";
    }

    lateinit var presenter: UpdateGestureContracts.Presenter
    private lateinit var sensorDataListener: SensorData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_gesture)

        val name: String = intent.extras!!.get("name").toString()
        val id_function: Int = intent.extras!!.get("id_function").toString().toInt()

        textViewUpdateGestureName.text = name
        textViewUpdateGestureFunction.text = Function.mapFunctionToName[id_function].toString()

        textViewUpdateGestureMessages.setMovementMethod(ScrollingMovementMethod());
        samples_saved = 0


        handler = Handler(Looper.getMainLooper())
        presenter = UpdateGesturePresenter(this, this, handler)


        myLineChart = MyLineChart(lineChartUpdateGesture, "Sensor Measures")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorDataListener = SensorData(myLineChart, this, handler, baseContext)

        checkBoxShowAccelerometerChart.setOnCheckedChangeListener { compoundButton, check -> sensorDataListener.setCheckedAccelerometer(check) }

        checkBoxShowGyroscopeChart.setOnCheckedChangeListener { compoundButton, check -> sensorDataListener.setCheckedGyroscope(check) }


        buttonUpdateGestureStartRecording.setOnClickListener {
            sensorDataListener.startRecording(checkBoxShowAccelerometerChart.isChecked, checkBoxShowGyroscopeChart.isChecked)

        }

        buttonUpdateGestureSaveSample.setOnClickListener {
            presenter.onSaveSample(
                sensorDataListener.getQueueAccelerometerX(), sensorDataListener.getQueueAccelerometerY(),
                sensorDataListener.getQueueAccelerometerZ(), sensorDataListener.getQueueGyroscopeX(),
                sensorDataListener.getQueueGyroscopeY(), sensorDataListener.getQueueGyroscopeZ()
            )
        }

        imageButtonUpdateGestureConfig.setOnClickListener {
            startActivity(Intent(this, NNConfigParametersActivity::class.java))
        }


        buttonUpdateGestureCommitSave.setOnClickListener {

            val username_user = User.loggedUser!!.username

            if(validateDataGesture(name, id_function, username_user)){
                presenter.onCommitSaveGesture(id_function!!, username_user, name)
            }else{
                GlobalMethods.showShortToast(this, getString(R.string.msg_invalid_data))
            }

        }

        buttonUpdateGestureBack.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun validateDataGesture(
        name: String,
        idFunction: Int?,
        username_user: String
    ): Boolean {
        return !name.equals("") && idFunction != null && !username_user.equals("")
    }

    override fun disableButtons() {
        buttonUpdateGestureStartRecording.isEnabled = false
        buttonUpdateGestureSaveSample.isEnabled = false
        buttonUpdateGestureBack.isEnabled = false
    }

    override fun enableButtons() {
        buttonUpdateGestureStartRecording.isEnabled = true
        buttonUpdateGestureSaveSample.isEnabled = true
        buttonUpdateGestureBack.isEnabled = true
    }

    override fun updateTextViewMessage(text: String) {
        textViewUpdateGestureMessages.text = text
    }

    override fun onRamSavedSample() {
        samples_saved++;
        if(samples_saved == Parameters.MAX_SAMPLES_SAVED_FOR_TRAINING){

            GlobalMethods.showLongToast(this, "Se han guardado todos los ejemplos necesarios, dele un nombre, una función y pulse el botón de añadir")
            disableButtons()
            buttonUpdateGestureCommitSave.isEnabled = true
        }
    }

    override fun disableButtonSaveSample() {
        buttonUpdateGestureSaveSample.isEnabled = false
    }

    override fun back() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorDataListener, gyroscope, Parameters.TIME_PER_GESTURE_MEASURE_MS)
        sensorManager.registerListener(sensorDataListener, linearAccelerometer, Parameters.TIME_PER_GESTURE_MEASURE_MS)

        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorDataListener, linearAccelerometer)
        sensorManager.unregisterListener(sensorDataListener, gyroscope)

        presenter.onPause()
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun onMessage(msg: String) {
        handler.post{
            textViewUpdateGestureMessages.text = msg
        }
    }

    override fun onStreamPrediction(prediction: String, predictFunction: Int) {
        // esta clase no hará streaming
    }

    override fun onRecordingFinished() {
        handler.post {
            enableButtons()
        }
    }



}