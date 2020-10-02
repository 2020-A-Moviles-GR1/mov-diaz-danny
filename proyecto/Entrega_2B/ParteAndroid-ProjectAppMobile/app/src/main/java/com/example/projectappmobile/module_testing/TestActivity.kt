package com.example.projectappmobile.module_testing

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectappmobile.R
import com.example.projectappmobile.models.MyLineChart
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.neural_network.*
import kotlinx.android.synthetic.main.activity_test.*
import java.lang.Exception
import java.lang.reflect.Parameter
import kotlin.collections.HashMap


class TestActivity : AppCompatActivity(), TestContracts.View {

    companion object {
        const val TAG = "TestActivity"
        var gesture_created = false
    }


    private var view_values: Boolean = false;


    private lateinit var myLineChart: MyLineChart
    private var view_all_plot_data = true





    private lateinit var presenter: TestContracts.Presenter

    private lateinit var handler: Handler

    private val spinnerMap = HashMap<Int, Int>()


    lateinit var username_user: String;




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        username_user = "user_test";


        handler = Handler(Looper.getMainLooper())
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        myLineChart = MyLineChart(findViewById(R.id.lineChartTest), "Sensor Measures")
        presenter = TestPresenter(this, this, handler,
            sensorManager, linearAccelerometer, gyroscope, username_user)
        presenter.onPopulateSpinner()

        initViews()
    }

    private fun initViews() {
        textViewTestLogNN.setMovementMethod(ScrollingMovementMethod());
        linearLayoutTestPrecisionSensor.visibility = View.GONE

        editTextTestThresholdSize.setText("${Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY}")
        editTextTestThresholdVelocity.setText("${Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY}")
        linearLayoutTestControlRecord.requestFocus()
        // RECORD SECTION
        buttonTestRecord.setOnClickListener {
            view_all_plot_data = true
            presenter.onRecordClick(checkBoxTestShowAccelerometerChart.isChecked, checkBoxTestShowGyroscopeChart.isChecked)
        }
        buttonTestUpload.setOnClickListener{
            val id_function: Int? = spinnerMap.get(spinnerTestFunctions.getSelectedItemPosition());
            if (id_function != null) {
                val filename = "data,$username_user,function$id_function.txt";
                presenter.onUploadSample(username_user, id_function, filename)
            }else{
                showShortToast("Error eligiendo la función")
                return@setOnClickListener
            }
        }
        buttonTestResetDataForFunction.setOnClickListener{
            val id_function: Int? = spinnerMap.get(spinnerTestFunctions.getSelectedItemPosition());
            // showShortToast("Id: $id_function")
            if (id_function != null) {
                presenter.onResetLocalDataForFunction(username_user, id_function)
            }else{
                showShortToast("Error eligiendo la función")
                return@setOnClickListener
            }
        }

        // STREAMING AND RESUME DATA SECTION
        buttonTestResumeData.setOnClickListener{
            presenter.onResumeDataPressed(username_user, mapFuntionToName)
        }
        toggleButtonTestSTREAM.setOnCheckedChangeListener{
                view, checked ->
                    run {
                        if (checked) {
                            val new_size = editTextTestThresholdSize.text.toString();
                            val new_mean_velocity = editTextTestThresholdSize.text.toString()
                            if(!new_size.equals("") && !new_mean_velocity.equals("")){
                                try {
                                    Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY = new_size.toByte()
                                    Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY = new_mean_velocity.toByte()
                                }catch (error: Exception){
                                    Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY = 20
                                    Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY = 10
                                }
                            }
                            view_all_plot_data = false
                            presenter.onStreamBegin(checkBoxTestShowAccelerometerChart.isChecked, checkBoxTestShowGyroscopeChart.isChecked)
                            editTextTestThresholdSize.visibility = View.GONE
                            editTextTestThresholdVelocity.visibility = View.GONE
                        } else {
                            view_all_plot_data = true
                            presenter.onStreamStop()
                            editTextTestThresholdSize.visibility = View.VISIBLE
                            editTextTestThresholdVelocity.visibility = View.VISIBLE
                        }
                    }

        }

        // Neural Network Section
        buttonTestTrainModel.setOnClickListener{
            val lambda = 0.0;
            val alpha = 0.1;
            val epochs = 5;
            val samples = -1;
            val normalize = true;
            val shuffle = true;
            val verbose = true;
            val augmentation_by_shifting_percent_of_data = 0.05;
            val augmentation_by_move_average_box_begin = 12;
            val augmentation_by_move_average_box_end = 20;
            val train_split_portion = 0.7;
            val validation_split_portion = 0.1;
            val random_seed_shuffle: Long = 420;
            val random_seed_init_weights: Long = 248;
            val std_objective_init_weights = 1.0;
            val mean_objective_init_weights = 0.0;
            val neurons_layers_init_arquitecture = intArrayOf(-1, 35, 18, -1)

            val config = NeuralNetwork.Config(
                lambda, alpha, epochs, samples, normalize, shuffle, verbose,
                augmentation_by_shifting_percent_of_data, augmentation_by_move_average_box_begin,
                augmentation_by_move_average_box_end, train_split_portion, validation_split_portion,
                random_seed_shuffle, random_seed_init_weights, std_objective_init_weights, mean_objective_init_weights,
                neurons_layers_init_arquitecture
            )

            presenter.onTrainModelPressed(username_user, mapFuntionToName, config, textViewTestLogNN)


        }
        buttonTestTestOne.setOnClickListener{
            presenter.onPredictOne()
        }
        buttonTestInfoNN.setOnClickListener{
            presenter.onInfoNNPressed()
        }
        buttonTestSaveModel.setOnClickListener{
            showShortToast("Saving model, it can took 3-5 minutes, dont shut down the phone")
            presenter.onSaveModel()
        }
        buttonTestResetAll.setOnClickListener{
            presenter.onResetAll(username_user)
        }
        buttonTestIPChange.setOnClickListener {
            val ip = editTextTestChangeIP.getText().toString()
            if(ip.equals("")){
                return@setOnClickListener
            }
            Parameters.IP_SERVER = ip
        }

    }


    override fun showLongToast(output: String) {
        if(!output.equals(""))
            Toast.makeText(this, output, Toast.LENGTH_LONG).show()
    }

    override fun updateTextViewLogNN(log: String) {
        handler.post {
            textViewTestLogNN.text = log;
        }
    }

    override fun enableButtonUpload() {
        buttonTestUpload.isEnabled = true
    }

    override fun enableButtonSaveModel() {
        buttonTestSaveModel.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestruct()
    }


    override fun showShortToast(msg: String) {
        if(!msg.equals(""))
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun enableButtonRecord() {
        buttonTestRecord.isEnabled = true
    }


    override fun disableButtonsNN() {
        buttonTestTrainModel.isEnabled = false
        buttonTestTestOne.isEnabled = false
        buttonTestInfoNN.isEnabled = false
        buttonTestResetAll.visibility = View.INVISIBLE
        buttonTestSaveModel.isEnabled = false
    }

    override fun disableButtonsRecordingForFunction() {
        buttonTestRecord.isEnabled = false
        buttonTestUpload.isEnabled = false
        buttonTestResetDataForFunction.isEnabled = false
    }

    override fun enableButtonsNN() {
        buttonTestTrainModel.isEnabled = true
        buttonTestTestOne.isEnabled = true
        buttonTestInfoNN.isEnabled = true
        buttonTestResetAll.visibility = View.VISIBLE
    }

    override fun enableButtonsRecordingForFunction() {
        buttonTestRecord.isEnabled = true
        buttonTestUpload.isEnabled = true
        buttonTestResetDataForFunction.isEnabled = true
    }

    override fun showToggleButtonStream(){
        toggleButtonTestSTREAM.visibility = View.VISIBLE
    }

    override fun hideToggleButtonStream(){
        toggleButtonTestSTREAM.visibility = View.INVISIBLE
    }

    override fun enableButtonResetFunction(){
        buttonTestResetDataForFunction.isEnabled = true
    }

    override fun updateTextViewResult(text: String) {
        handler.post {
            textViewTestResult.text = text
        }
    }

    override fun clearLineChart(preloadAccelerometer: Boolean, preloadGyroscope:Boolean) {
        lineChartTest.clearValues()
        lineChartTest.data.clearValues()
        lineChartTest.legend.resetCustom()
        myLineChart.lineChart.data.clearValues()

        myLineChart = MyLineChart(lineChartTest, "Gráfica de sensores")

        if(preloadAccelerometer){
            myLineChart.addEntry(0f, "Acc X", Color.RED, 0, view_all_plot_data)
            myLineChart.addEntry(0f, "Acc Y", Color.GREEN, 1, view_all_plot_data)
            myLineChart.addEntry(0f, "Acc Z", Color.BLUE, 2, view_all_plot_data)
        }
        if(preloadGyroscope){
            myLineChart.addEntry(0f, "Gyro X", Color.MAGENTA, 3, view_all_plot_data)
            myLineChart.addEntry(0f, "Gyro Y", Color.YELLOW, 4, view_all_plot_data)
            myLineChart.addEntry(0f, "Gyro Z", Color.CYAN, 5, view_all_plot_data)

        }


    }

    override fun plotDataAccelerometer(
        accelerometerX: Float,
        accelerometerY: Float,
        accelerometerZ: Float
    ) {
        if(view_values){
            textViewTestAccelerometerX.text = String.format(getString(R.string.format_x_value), accelerometerX)
            textViewTestAccelerometerY.text = String.format(getString(R.string.format_y_value), accelerometerY)
            textViewTestAccelerometerZ.text = String.format(getString(R.string.format_z_value), accelerometerZ)
        }



        myLineChart.addEntry(accelerometerX, "Acc X", Color.RED, 0, view_all_plot_data)
        myLineChart.addEntry(accelerometerY, "Acc Y", Color.GREEN, 1, view_all_plot_data)
        myLineChart.addEntry(accelerometerZ, "Acc Z", Color.BLUE, 2, view_all_plot_data)

    }

    override fun plotDataGyroscope(gyroscopeX: Float, gyroscopeY: Float, gyroscopeZ: Float) {
        if(view_values){
            textViewTestGyroscopeX.text = String.format(getString(R.string.format_x_value), gyroscopeX)
            textViewTestGyroscopeY.text = String.format(getString(R.string.format_y_value), gyroscopeY)
            textViewTestGyroscopeZ.text = String.format(getString(R.string.format_z_value), gyroscopeZ)
        }

        myLineChart.addEntry(gyroscopeX, "Gyro X", Color.MAGENTA, 3, view_all_plot_data)
        myLineChart.addEntry(gyroscopeY, "Gyro Y", Color.YELLOW, 4, view_all_plot_data)
        myLineChart.addEntry(gyroscopeZ, "Gyro Z", Color.CYAN, 5, view_all_plot_data)

    }


    lateinit var mapFuntionToName: HashMap<Int, Any>;

    override fun populateSpinner(functions: List<Function>?) {
        val spinnerArray = arrayListOf<String?>()
        mapFuntionToName = HashMap<Int, Any>()
        if (functions != null) {
            for (i in 0 until functions.size) {
                spinnerMap.put(i, functions.get(i).id)
                spinnerArray.add(functions.get(i).name)

                mapFuntionToName.put(functions.get(i).id, functions.get(i).name)
            }
        }


        val adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTestFunctions.setAdapter(adapter)
    }




}