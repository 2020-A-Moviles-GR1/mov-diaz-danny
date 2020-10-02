package com.example.projectappmobile.module_game_room.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.R
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.SensorData
import com.example.projectappmobile.module_game_room.contracts.ConnectRoomContracts
import com.example.projectappmobile.module_game_room.presenter.ConnectRoomPresenter
import kotlinx.android.synthetic.main.activity_connect_room.*


class ConnectRoomActivity : AppCompatActivity(), ConnectRoomContracts.View, SensorData.SensorDataInterface{

    private lateinit var gyroscope: Sensor
    private lateinit var linearAccelerometer: Sensor
    private lateinit var sensorManager: SensorManager
    private var room: Int = -999

    lateinit var presenter: ConnectRoomContracts.Presenter
    lateinit var handler: Handler
    private lateinit var sensorDataListener: SensorData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_room)
        handler = Handler(Looper.getMainLooper())



        presenter = ConnectRoomPresenter(this, this, handler)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorDataListener = SensorData(null, this, handler, baseContext)

        buttonConnectRoomGetIn.setOnClickListener{
            val text = editTextConnectRoomNumber.text.toString()
            if(text.equals("")){
                return@setOnClickListener
            }
            val room_number: Int = text.toInt()

            presenter.onSendRoomNumber(room_number)
        }

        buttonConnectRoomBack.setOnClickListener{
            finish()
        }


        // Layout config 2
        imageButtonConnectRoomConfig.setOnClickListener{
            startActivity(Intent(this, FreeModeActivity::class.java))
        }

        imageButtonConnectRoomInfo.setOnClickListener{
            TODO("MOSTRAR MENSAJE CON INFORMACIÓN")
        }

        buttonConnectRoomBack2.setOnClickListener{
            finish()
        }
    }


    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()

        if(room != -999){

            sensorManager.registerListener(sensorDataListener, gyroscope, Parameters.TIME_PER_GESTURE_MEASURE_MS)
            sensorManager.registerListener(sensorDataListener, linearAccelerometer, Parameters.TIME_PER_GESTURE_MEASURE_MS)

            sensorDataListener.streamBegin(false, false)

        }

    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()

        sensorManager.unregisterListener(sensorDataListener, linearAccelerometer)
        sensorManager.unregisterListener(sensorDataListener, gyroscope)

        sensorDataListener.streamStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }


    override fun enableButtons() {
        buttonConnectRoomGetIn.isEnabled = true
        buttonConnectRoomBack.isEnabled = true
    }

    override fun onSocketFailure(euphemiseMessage: String) {
        GlobalMethods.showLongToast(this, euphemiseMessage)
        super.onBackPressed()
    }

    override fun disableButtons() {
        buttonConnectRoomGetIn.isEnabled = false
        buttonConnectRoomBack.isEnabled = false
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun showGameRoom(room_number: Int) {
        linearLayoutConnectRoomValidation.visibility = View.GONE
        imageViewConnectRoom.visibility = View.GONE
        editTextConnectRoomNumber.clearFocus()
        hideKeyboard(this)

        textViewConnectRoomRoomNumber.text = "Room: $room_number"


        linearLayoutConnectRoomGameRoom.visibility = View.VISIBLE
        imageViewConnectRoomGameRoom.visibility = View.VISIBLE
        buttonConnectRoomBack2.visibility = View.VISIBLE


        room = room_number

        sensorManager.registerListener(sensorDataListener, gyroscope, Parameters.TIME_PER_GESTURE_MEASURE_MS)
        sensorManager.registerListener(sensorDataListener, linearAccelerometer, Parameters.TIME_PER_GESTURE_MEASURE_MS)

        sensorDataListener.streamBegin(false, false)
    }

    override fun onMessage(msg: String) {
        handler.post{
            GlobalMethods.showShortToast(this, msg)
        }
    }

    override fun onStreamPrediction(prediction: String, predictFunction: Int) {
        Log.i("ConnectRoomActivity", prediction)
        handler.post{
            presenter.onStreamPrediction(predictFunction, room)
        }

    }

    override fun onRecordingFinished() {
        // N/A aquí
    }


}