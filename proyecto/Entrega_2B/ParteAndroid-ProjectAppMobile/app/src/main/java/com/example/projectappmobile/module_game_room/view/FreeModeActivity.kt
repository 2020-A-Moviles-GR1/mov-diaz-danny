package com.example.projectappmobile.module_game_room.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.projectappmobile.R
import com.example.projectappmobile.models.MyLineChart
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.SensorData
import kotlinx.android.synthetic.main.activity_free_mode.*

class FreeModeActivity : AppCompatActivity(), SensorData.SensorDataInterface {


    private lateinit var gyroscope: Sensor
    private lateinit var linearAccelerometer: Sensor
    private lateinit var sensorManager: SensorManager
    private var sensibility1 = Parameters.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY
    private var sensibility2 = Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY
    private var sensibility3 = Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY




    private lateinit var myLineChart: MyLineChart
    private lateinit var handler: Handler


    companion object {
        const val TAG: String = "FreeModeActivity";
    }


    private lateinit var sensorDataListener: SensorData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)

        myLineChart = MyLineChart(lineChartFreeMode, "Sensor Measures")

        handler = Handler(Looper.getMainLooper())
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorDataListener = SensorData(myLineChart, this, handler, baseContext)



        rangeSeekbarFreeModeSensibility1.minStartValue = sensibility1.toFloat()
        rangeSeekbarFreeModeSensibility2.minStartValue = sensibility2.toFloat()
        rangeSeekbarFreeModeSensibility3.minStartValue = sensibility3.toFloat()


        textViewSensibility1.text = getString(R.string.text_sensibility_1) + ": $sensibility1"
        textViewSensibility2.text = getString(R.string.text_sensibility_2) + ": $sensibility2"
        textViewSensibility3.text = getString(R.string.text_sensibility_3) + ": $sensibility3"


        rangeSeekbarFreeModeSensibility1.setOnSeekbarChangeListener {
            textViewSensibility1.text = getString(R.string.text_sensibility_1) + ": $sensibility1"
        }

        rangeSeekbarFreeModeSensibility2.setOnSeekbarChangeListener {
            textViewSensibility2.text = getString(R.string.text_sensibility_2) + ": $sensibility2"
        }

        rangeSeekbarFreeModeSensibility3.setOnSeekbarChangeListener {
            textViewSensibility3.text = getString(R.string.text_sensibility_3) + ": $sensibility3"
        }

        rangeSeekbarFreeModeSensibility1.setOnSeekbarFinalValueListener {
            sensibility1 = it.toByte()

        }

        rangeSeekbarFreeModeSensibility2.setOnSeekbarFinalValueListener {
            sensibility2 = it.toByte()

        }

        rangeSeekbarFreeModeSensibility3.setOnSeekbarFinalValueListener {
            sensibility3 = it.toByte()

        }

        checkBoxShowAccelerometerChart.setOnCheckedChangeListener { compoundButton, check -> sensorDataListener.setCheckedAccelerometer(check) }

        checkBoxShowGyroscopeChart.setOnCheckedChangeListener { compoundButton, check -> sensorDataListener.setCheckedGyroscope(check) }

        buttonFreeModeSaveConfig.setOnClickListener {
            Parameters.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY = sensibility1
            Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY = sensibility2
            Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY = sensibility3
            //todo: guardar en shared preferencess y consultar los parametros desde shared preferences
            textViewFreeModeMessages.text = getString(R.string.msg_saved_successfully)
        }

        buttonFreeModeBack.setOnClickListener {
            super.onBackPressed()
        }



    }



    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(sensorDataListener, gyroscope, Parameters.TIME_PER_GESTURE_MEASURE_MS)
        sensorManager.registerListener(sensorDataListener, linearAccelerometer, Parameters.TIME_PER_GESTURE_MEASURE_MS)

        sensorDataListener.streamBegin(checkBoxShowAccelerometerChart.isChecked, checkBoxShowGyroscopeChart.isChecked)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorDataListener, linearAccelerometer)
        sensorManager.unregisterListener(sensorDataListener, gyroscope)

        sensorDataListener.streamStop()
    }

    override fun onMessage(msg: String) {
        handler.post{
            textViewFreeModeMessages.text = msg
        }
    }

    override fun onStreamPrediction(prediction: String, predictFunction: Int) {
        handler.post{
            textViewFreeModeMessages.text = prediction
        }

    }

    override fun onRecordingFinished() {
        // N / A para esta clase
    }


}