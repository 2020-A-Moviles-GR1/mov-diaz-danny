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
import android.widget.ArrayAdapter
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.R
import com.example.projectappmobile.models.MyLineChart
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.SensorData
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.module_gestures.NNConfigParametersActivity
import com.example.projectappmobile.module_gestures.contracts.AddGestureContracts
import com.example.projectappmobile.module_gestures.presenter.AddGesturePresenter
import kotlinx.android.synthetic.main.activity_add_gesture.*
import kotlinx.android.synthetic.main.activity_add_gesture.checkBoxShowAccelerometerChart
import kotlinx.android.synthetic.main.activity_add_gesture.checkBoxShowGyroscopeChart
import kotlinx.android.synthetic.main.activity_add_gesture.progressBar
import java.util.HashMap

class AddGestureActivity : AppCompatActivity(), SensorData.SensorDataInterface, AddGestureContracts.View {

    private lateinit var gyroscope: Sensor
    private lateinit var linearAccelerometer: Sensor
    private lateinit var sensorManager: SensorManager

    private lateinit var myLineChart: MyLineChart
    private lateinit var handler: Handler

    private var samples_saved: Byte = 0

    companion object {
        const val TAG: String = "AddGestureActivity";
    }

    lateinit var presenter: AddGestureContracts.Presenter
    private lateinit var sensorDataListener: SensorData

    private var spinnerMap = HashMap<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_gesture)

        textViewAddGestureMessages.setMovementMethod(ScrollingMovementMethod());
        samples_saved = 0


        handler = Handler(Looper.getMainLooper())
        presenter = AddGesturePresenter(this, this, handler)


        myLineChart = MyLineChart(lineChartAddGesture, "Sensor Measures")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorDataListener = SensorData(myLineChart, this, handler, baseContext)

        checkBoxShowAccelerometerChart.setOnCheckedChangeListener { compoundButton, check -> sensorDataListener.setCheckedAccelerometer(check) }

        checkBoxShowGyroscopeChart.setOnCheckedChangeListener { compoundButton, check -> sensorDataListener.setCheckedGyroscope(check) }


        buttonAddGestureStartRecording.setOnClickListener {
            sensorDataListener.startRecording(checkBoxShowAccelerometerChart.isChecked, checkBoxShowGyroscopeChart.isChecked)

        }

        buttonAddGestureSaveSample.setOnClickListener {
            presenter.onSaveSample(
                sensorDataListener.getQueueAccelerometerX(), sensorDataListener.getQueueAccelerometerY(),
                sensorDataListener.getQueueAccelerometerZ(), sensorDataListener.getQueueGyroscopeX(),
                sensorDataListener.getQueueGyroscopeY(), sensorDataListener.getQueueGyroscopeZ()
            )
        }
        imageButtonAddGestureConfig.setOnClickListener {
            startActivity(Intent(this, NNConfigParametersActivity::class.java))
        }


        buttonAddGestureCommitSave.setOnClickListener {
            val name = editTextAddGestureName.text.toString()
            val id_function = spinnerMap[spinnerAddGestureFunctionsList.selectedItemPosition]
            val username_user = User.loggedUser!!.username

            if(validateDataGesture(name, id_function, username_user)){
                presenter.onCommitSaveGesture(id_function!!, username_user, name)
            }else{
                GlobalMethods.showShortToast(this, getString(R.string.msg_invalid_data))
            }

        }

        buttonAddGestureBack.setOnClickListener {
            super.onBackPressed()
        }

        presenter.onPopulateSpinnerFunctions()

    }

    private fun validateDataGesture(
        name: String,
        idFunction: Int?,
        username_user: String
    ): Boolean {
        return !name.equals("") && idFunction != null && !username_user.equals("")
    }

    override fun disableButtons() {
        buttonAddGestureStartRecording.isEnabled = false
        buttonAddGestureSaveSample.isEnabled = false
        buttonAddGestureBack.isEnabled = false
    }

    override fun enableButtons() {
        buttonAddGestureStartRecording.isEnabled = true
        buttonAddGestureSaveSample.isEnabled = true
        buttonAddGestureBack.isEnabled = true
    }

    override fun updateTextViewMessage(text: String) {
        textViewAddGestureMessages.text = text
    }

    override fun populateSpinner(functions: List<Function>) {
        val spinnerArray = arrayListOf<String?>()
        Function.mapFunctionToName = HashMap()
        spinnerMap = HashMap()

        for (i in 0 until functions.size) {
            spinnerMap.put(i, functions.get(i).id)
            spinnerArray.add(functions.get(i).name)

            Function.mapFunctionToName.put(functions.get(i).id, functions.get(i).name)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddGestureFunctionsList.setAdapter(adapter)


    }

    override fun onRamSavedSample() {
        samples_saved++;
        if(samples_saved == Parameters.MAX_SAMPLES_SAVED_FOR_TRAINING){

            GlobalMethods.showLongToast(this, "Se han guardado todos los ejemplos necesarios, dele un nombre, una función y pulse el botón de añadir")
            disableButtons()
            buttonAddGestureCommitSave.isEnabled = true
        }
    }

    override fun disableButtonSaveSample() {
        buttonAddGestureSaveSample.isEnabled = false
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
            textViewAddGestureMessages.text = msg
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