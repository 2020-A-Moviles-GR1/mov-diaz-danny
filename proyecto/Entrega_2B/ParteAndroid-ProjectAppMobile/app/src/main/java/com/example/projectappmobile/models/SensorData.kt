package com.example.projectappmobile.models

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.example.projectappmobile.R
import com.example.projectappmobile.models.neural_network.NeuralNetwork
import com.example.projectappmobile.models.neural_network.fusionVectorByHorizontal
import com.example.projectappmobile.module_testing.TestInteractor
import java.util.*
import kotlin.math.abs

class SensorData(
    private val myLineChart: MyLineChart?,
    private val presenter: SensorDataInterface,
    private val handler: Handler,
    private val mContext: Context
) : SensorEventListener {

    interface SensorDataInterface {
        fun onMessage(msg: String)
        fun onStreamPrediction(prediction: String, predictFunction: Int)
        fun onRecordingFinished()

    }


    private var waiting_for_gyroscope: Boolean = false
    private var waiting_for_accelerometer: Boolean = false
    private var accelerometer_cuted: Boolean = false
    private var gyroscope_cuted: Boolean = false
    private var once: Boolean = true


    private var recordData = false
    private var plotDataAccelerometer = false
    private var plotDataGyroscope = false
    private var view_all_plot_data = true

    private var is_streaming: Boolean = false

    private var checkedPlotAccelerometer: Boolean = false;
    private var checkedPlotGyroscope: Boolean = false;

    private var neuralNetwork: NeuralNetwork = NeuralNetwork()


    private var queueAccelerometerX = LinkedList<Double>()
    private var queueAccelerometerY = LinkedList<Double>()
    private var queueAccelerometerZ = LinkedList<Double>()
    private var queueGyroscopeX = LinkedList<Double>()
    private var queueGyroscopeY = LinkedList<Double>()
    private var queueGyroscopeZ = LinkedList<Double>()

    fun getQueueAccelerometerX(): LinkedList<Double>{
        return queueAccelerometerX
    }
    fun getQueueAccelerometerY(): LinkedList<Double>{
        return queueAccelerometerY
    }
    fun getQueueAccelerometerZ(): LinkedList<Double>{
        return queueAccelerometerZ
    }
    fun getQueueGyroscopeX(): LinkedList<Double>{
        return queueGyroscopeX
    }
    fun getQueueGyroscopeY(): LinkedList<Double>{
        return queueGyroscopeY
    }
    fun getQueueGyroscopeZ(): LinkedList<Double>{
        return queueGyroscopeZ
    }

    private var queueAccelerometerXForPredict = LinkedList<Double>()
    private var queueAccelerometerYForPredict = LinkedList<Double>()
    private var queueAccelerometerZForPredict = LinkedList<Double>()
    private var queueGyroscopeXForPredict = LinkedList<Double>()
    private var queueGyroscopeYForPredict = LinkedList<Double>()
    private var queueGyroscopeZForPredict = LinkedList<Double>()

    private var wait_delay_between_predictions = false

    private fun clearLineChart() {
        if(myLineChart != null){
            myLineChart.lineChart.clearValues()
            myLineChart.lineChart.legend.resetCustom()
            myLineChart.lineChart.data.clearValues()

            if(checkedPlotAccelerometer){
                myLineChart.addEntry(0f, "Acc X", Color.RED, 0, view_all_plot_data)
                myLineChart.addEntry(0f, "Acc Y", Color.GREEN, 1, view_all_plot_data)
                myLineChart.addEntry(0f, "Acc Z", Color.BLUE, 2, view_all_plot_data)
            }
            if(checkedPlotGyroscope){
                myLineChart.addEntry(0f, "Gyro X", Color.MAGENTA, 3, view_all_plot_data)
                myLineChart.addEntry(0f, "Gyro Y", Color.YELLOW, 4, view_all_plot_data)
                myLineChart.addEntry(0f, "Gyro Z", Color.CYAN, 5, view_all_plot_data)

            }
        }



    }

    fun startRecording(checkedPlotAccelerometer: Boolean, checkedPlotGyroscope: Boolean) {
        resetLists()
        clearLineChart()
        view_all_plot_data = true
        this.checkedPlotAccelerometer = checkedPlotAccelerometer
        this.checkedPlotGyroscope = checkedPlotGyroscope


        Thread(Runnable {
            var time_ms = 0

            var counter = Parameters.WAIT_TIME_S

            presenter.onMessage("Inicio en: $counter")

            while (counter > 0) {
                try {
                    Thread.sleep(1000)
                    counter -= 1
                    presenter.onMessage("Inicio en: $counter")
                } catch (e: Exception) {
                    e.message?.let { Log.e(TestInteractor.TAG, it) }
                }
            }

            handler.post {
                playSound(R.raw.beep)
            }

            recordData = true

            controlPlotDataAccelerometer()
            controlPlotDataGyroscope()

            try {
                Thread.sleep(Parameters.GESTURE_DURATION_MS.toLong())
                // puede no ser completado de forma exacta
            } catch (e: Exception) {
                e.message?.let { Log.e(TestInteractor.TAG, it) }
            }

            // barrera de seguridad por si tiene valores incompletos
            while (!isSetOfDataComplete()) {
                try {
                    time_ms += Parameters.TIME_PER_GESTURE_MEASURE_MS
                    // wait for one more sample
                } catch (e: Exception) {
                    e.message?.let { Log.e(TestInteractor.TAG, it) }
                }
            }

            recordData = false

            Log.i(TestInteractor.TAG, "Se recolectaron ${Parameters.GESTURE_NUMBER_MEASURES} ejemplares por eje de cada sensor")


            presenter.onMessage("Medición terminada")
            presenter.onRecordingFinished()

            handler.post{
                playSound(R.raw.confirmation)
            }
        }).start()
    }

    private fun isSetOfDataComplete(): Boolean{
        return queueAccelerometerX.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueAccelerometerY.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueAccelerometerZ.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueGyroscopeX.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueGyroscopeY.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueGyroscopeZ.size == Parameters.GESTURE_NUMBER_MEASURES
    }

    fun streamBegin(checkedPlotAccelerometer: Boolean, checkedPlotGyroscope: Boolean) {
        view_all_plot_data = false

        this.checkedPlotAccelerometer = checkedPlotAccelerometer
        this.checkedPlotGyroscope = checkedPlotGyroscope
        resetLists()
        clearLineChart()


        once = true
        recordData = true
        is_streaming = true
        controlPlotDataAccelerometer()
        controlPlotDataGyroscope()
    }

    fun streamStop() {
        view_all_plot_data = true
        recordData = false
        is_streaming = false
    }

    fun setCheckedAccelerometer(checked: Boolean){

        if(!this.checkedPlotAccelerometer && checked){
            //cambio
            this.checkedPlotAccelerometer = checked
            controlPlotDataAccelerometer()
        }else{
            this.checkedPlotAccelerometer = checked
        }

    }

    fun setCheckedGyroscope(checked: Boolean){

        if(!this.checkedPlotGyroscope && checked){
            //cambio
            this.checkedPlotGyroscope = checked
            controlPlotDataGyroscope()
        }else{
            this.checkedPlotGyroscope = checked
        }

    }


    private fun isPosibleGesture(
        queueX: LinkedList<Double>,
        queueY: LinkedList<Double>,
        queueZ: LinkedList<Double>
    ): Boolean {

        val meanX = (queueX.map { abs(it) }).sum()/Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY
        val meanY = (queueY.map { abs(it) }).sum()/Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY
        val meanZ = (queueZ.map { abs(it) }).sum()/Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY

        return meanX  > Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY ||
                meanY  > Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY ||
                meanZ  > Parameters.THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY


    }

    private var is_playing_sound: Boolean = false
    private fun playSound(sound: Int) {

        if(!is_playing_sound){
            val mp: MediaPlayer = MediaPlayer.create(mContext, sound)
            mp.setVolume(50f,50f);
            mp.setOnCompletionListener {
                it.stop()
                it.release()
                is_playing_sound = false
            }
            is_playing_sound = true
            mp.start()
        }
    }

    private fun resetLists(resetAccelerometer: Boolean = true, resetGyroscope: Boolean = true) {

        if(resetAccelerometer){
            queueAccelerometerX = LinkedList()
            queueAccelerometerY = LinkedList()
            queueAccelerometerZ = LinkedList()
        }

        if(resetGyroscope){
            queueGyroscopeX = LinkedList()
            queueGyroscopeY = LinkedList()
            queueGyroscopeZ = LinkedList()
        }

    }

    private fun recoverNN(): Boolean {
        if(!neuralNetwork.is_trained){
            val last_nn = NeuralNetwork.recoverNeuralNetwork(mContext);
            return if (last_nn != null){
                neuralNetwork = last_nn;
                true
            }else {
                presenter.onMessage("No hay ninguna red entrenada todavía")
                false
            }
        }
        return true
    }

    private fun predictFromStream() {
        if(wait_delay_between_predictions){
            return
        }

        if(!recoverNN()){
            return
        }

        if(queueAccelerometerZForPredict.size == 0 || queueGyroscopeZForPredict.size == 0){
            println("Un gesto debe ser reconocido antes, tiene datos vacios")
            // callbackPresenter.onPredictFailed("Un gesto debe ser reconocido antes, tiene datos vacios")
            return
        }
        val X = ArrayList<List<Double>>()
        X.add(
            fusionVectorByHorizontal(
                queueAccelerometerXForPredict, queueAccelerometerYForPredict, queueAccelerometerZForPredict,
                queueGyroscopeXForPredict, queueGyroscopeYForPredict, queueGyroscopeZForPredict
            )
        )



        val (predict_class, logits) = neuralNetwork.predict(X);
        Log.i(TestInteractor.TAG, "Class: $predict_class, logits: $logits")
        val predict_function: Int = neuralNetwork.mapClassToFunction.get(predict_class[0][0]).toString().toInt()
        val predict_name = neuralNetwork.mapFuntionToName[predict_function]


        presenter.onStreamPrediction("class: $predict_class, function: $predict_function\nPred: ${predict_name}", predict_function)


        queueAccelerometerXForPredict = LinkedList()
        queueAccelerometerYForPredict = LinkedList()
        queueAccelerometerZForPredict = LinkedList()

        queueGyroscopeXForPredict = LinkedList()
        queueGyroscopeYForPredict = LinkedList()
        queueGyroscopeZForPredict = LinkedList()


        waiting_for_gyroscope = false
        waiting_for_accelerometer = false
        once = true
        gyroscope_cuted = false
        accelerometer_cuted = false

        wait_delay_between_predictions = true

        Thread(Runnable {
            var counter = 0
            while (counter < Parameters.WAIT_TIME_FOR_NEXT_PREDICTION_S){
                try {
                    Thread.sleep(1000)
                    counter += 1

                }
                catch (e: Exception) {
                    e.message?.let { Log.e(TestInteractor.TAG, it) }
                }
            }
            wait_delay_between_predictions = false

        }).start()

        // streamStop()



    }

    private fun controlPlotDataAccelerometer() {
        if(myLineChart != null){
            Thread(Runnable {
                while (recordData && checkedPlotAccelerometer){
                    plotDataAccelerometer = true
                    try {
                        Thread.sleep((Parameters.TIME_PER_GESTURE_MEASURE_MS.toLong()))

                    }
                    catch (e: Exception) {
                        e.message?.let { Log.e(TestInteractor.TAG, it) }
                    }
                }
                plotDataAccelerometer = false
            }).start()
        }


    }

    private fun controlPlotDataGyroscope() {
        if(myLineChart != null){
            Thread(Runnable {
                while (recordData && checkedPlotGyroscope){
                    plotDataGyroscope = true
                    try {
                        Thread.sleep((Parameters.TIME_PER_GESTURE_MEASURE_MS.toLong()))
                    }
                    catch (e: Exception) {
                        e.message?.let { Log.e(TestInteractor.TAG, it) }
                    }
                }
                plotDataGyroscope = false
            }).start()
        }


    }

    private fun plotDataAccelerometer(accelerometerX: Float, accelerometerY: Float, accelerometerZ: Float) {

        if(myLineChart != null){
            if(plotDataAccelerometer){
                myLineChart.addEntry(accelerometerX, "Acc X", Color.RED, 0, view_all_plot_data)
                myLineChart.addEntry(accelerometerY, "Acc Y", Color.GREEN, 1, view_all_plot_data)
                myLineChart.addEntry(accelerometerZ, "Acc Z", Color.BLUE, 2, view_all_plot_data)
                plotDataAccelerometer = false
            }

        }

    }

    private fun plotDataGyroscope(gyroscopeX: Float, gyroscopeY: Float, gyroscopeZ: Float) {
        if(myLineChart != null){
            if(plotDataGyroscope){
                myLineChart.addEntry(gyroscopeX, "Gyro X", Color.MAGENTA, 3, view_all_plot_data)
                myLineChart.addEntry(gyroscopeY, "Gyro Y", Color.YELLOW, 4, view_all_plot_data)
                myLineChart.addEntry(gyroscopeZ, "Gyro Z", Color.CYAN, 5, view_all_plot_data)
                plotDataGyroscope = false
            }
        }

    }

    private fun cutAccelerometerData() {
        val len = queueAccelerometerZ.size
        val begin = len- Parameters.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY;


        queueAccelerometerX = LinkedList(queueAccelerometerX.subList(begin, len))
        queueAccelerometerY = LinkedList(queueAccelerometerY.subList(begin, len))
        queueAccelerometerZ = LinkedList(queueAccelerometerZ.subList(begin, len))

        println("CUT ACCELEROMETER -> SIZE: ${queueAccelerometerZ.size}")
        println("ACCELEROMETER:$queueAccelerometerX\n$queueAccelerometerY\n$queueAccelerometerZ\n")
        accelerometer_cuted = true

    }

    private fun cutGyroscopeData() {
        val len = queueGyroscopeZ.size
        val begin = len- Parameters.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY;


        queueGyroscopeX = LinkedList(queueGyroscopeX.subList(begin, len))
        queueGyroscopeY = LinkedList(queueGyroscopeY.subList(begin, len))
        queueGyroscopeZ = LinkedList(queueGyroscopeZ.subList(begin, len))

        println("CUT GYROSCOPE-> SIZE: ${queueGyroscopeZ.size}")
        println("GYROSCOPE:$queueGyroscopeX\n$queueGyroscopeY\n$queueGyroscopeZ\n")
        gyroscope_cuted = true
    }



    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }

    override fun onSensorChanged(event: SensorEvent?) {

        val sensor:Sensor? = event?.sensor

        if(sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION){
            val accelerometer_x: Float = (event.values?.get(0) ?: Parameters.INVALID_SENSOR_VALUE)
            val accelerometer_y: Float = (event.values?.get(1) ?: Parameters.INVALID_SENSOR_VALUE)
            val accelerometer_z: Float = (event.values?.get(2) ?: Parameters.INVALID_SENSOR_VALUE)


            if(!wait_delay_between_predictions){
                onAccelerometerChange(accelerometer_x, accelerometer_y, accelerometer_z)
            }

        }
        else if(sensor?.type == Sensor.TYPE_GYROSCOPE){
            val gyroscope_x = (event.values?.get(0) ?: Parameters.INVALID_SENSOR_VALUE)
            val gyroscope_y = (event.values?.get(1) ?: Parameters.INVALID_SENSOR_VALUE)
            val gyroscope_z = (event.values?.get(2) ?: Parameters.INVALID_SENSOR_VALUE)
            if(!wait_delay_between_predictions){
                onGyroscopeChange(gyroscope_x, gyroscope_y, gyroscope_z)
            }
        }
    }


    private fun onAccelerometerChange(
        accelerometer_x: Float,
        accelerometer_y: Float,
        accelerometer_z: Float
    ) {

        if(recordData && !waiting_for_gyroscope){
            recordDataOfAccelerometer(accelerometer_x, accelerometer_y, accelerometer_z)
            plotDataAccelerometer(accelerometer_x, accelerometer_y, accelerometer_z)

            val recorded_accelerometer_size = queueAccelerometerZ.size
            if(is_streaming && recorded_accelerometer_size >= Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY){

                if(once){
                    if(isPosibleGesture(
                            LinkedList(queueAccelerometerX.subList(recorded_accelerometer_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, recorded_accelerometer_size)),
                            LinkedList(queueAccelerometerY.subList(recorded_accelerometer_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, recorded_accelerometer_size)),
                            LinkedList(queueAccelerometerZ.subList(recorded_accelerometer_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, recorded_accelerometer_size))
                        ) ){
                        println("Supero en Accelerometro")
                        //streamStop()
                        once = false
                        handler.post{
                            playSound(R.raw.beep)
                        }
                    }
                }
                if(!once){
                    if(accelerometer_cuted){
                        if(recorded_accelerometer_size == Parameters.GESTURE_NUMBER_MEASURES){
                            handler.post{
                                playSound(R.raw.confirmation)
                            }
                            println("ACCELEROMETER FOR PREDICTION:\naccx = $queueAccelerometerX;\naccy = $queueAccelerometerY;\naccz = $queueAccelerometerZ;\n")

                            queueAccelerometerXForPredict = LinkedList(queueAccelerometerX)
                            queueAccelerometerYForPredict = LinkedList(queueAccelerometerY)
                            queueAccelerometerZForPredict = LinkedList(queueAccelerometerZ)

                            resetLists(true, false)
                            println("Accelerometer empty with: ${queueAccelerometerZ.size} elements y predict copy con ${queueAccelerometerZForPredict.size}" )


                            if(queueGyroscopeZForPredict.size == Parameters.GESTURE_NUMBER_MEASURES){
                                predictFromStream()
                            }else{
                                waiting_for_gyroscope = true
                            }

                        }
                    }else{
                        if(recorded_accelerometer_size >= Parameters.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY){
                            cutAccelerometerData()
                        }
                    }
                }

            }
        }


    }


    private fun onGyroscopeChange(gyroscope_x: Float, gyroscope_y: Float, gyroscope_z: Float) {
        if(recordData && !waiting_for_accelerometer) {

            recordDataOfGyroscope(gyroscope_x, gyroscope_y, gyroscope_z)
            plotDataGyroscope(gyroscope_x, gyroscope_y, gyroscope_z)

            val recorded_gyroscope_size = queueGyroscopeZ.size

            if(is_streaming && recorded_gyroscope_size >= Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY){

                if(once){
                    if(isPosibleGesture(
                            LinkedList(queueGyroscopeX.subList(recorded_gyroscope_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, recorded_gyroscope_size)),
                            LinkedList(queueGyroscopeY.subList(recorded_gyroscope_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, recorded_gyroscope_size)),
                            LinkedList(queueGyroscopeZ.subList(recorded_gyroscope_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, recorded_gyroscope_size))
                        )
                    ){
                        println("Supero en Gyroscopio")
                        //streamStop()
                        once = false
                        handler.post{
                            playSound(R.raw.beep)
                        }
                    }
                }

                if(!once){
                    if(gyroscope_cuted){
                        if(recorded_gyroscope_size == Parameters.GESTURE_NUMBER_MEASURES){
                            handler.post{
                                playSound(R.raw.confirmation)
                            }
                            println("GYROSCOPE FOR PREDICTION:\ngyrX = $queueGyroscopeX;\ngyrY = $queueGyroscopeY;\ngyrZ = $queueGyroscopeZ;\n")

                            queueGyroscopeXForPredict = LinkedList(queueGyroscopeX)
                            queueGyroscopeYForPredict = LinkedList(queueGyroscopeY)
                            queueGyroscopeZForPredict = LinkedList(queueGyroscopeZ)


                            resetLists(false, true)
                            println("Gyroscope empty with: ${queueGyroscopeZ.size} elements" )

                            if(queueGyroscopeZForPredict.size == Parameters.GESTURE_NUMBER_MEASURES){
                                predictFromStream()
                            }else{
                                waiting_for_accelerometer = true
                            }

                        }
                    }else{
                        if(recorded_gyroscope_size >= Parameters.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY){
                            cutGyroscopeData()
                        }
                    }
                }

            }

        }

    }


    private fun recordDataOfAccelerometer(accelerometer_x: Float, accelerometer_y: Float, accelerometer_z: Float) {
        if(queueAccelerometerX.size == Parameters.GESTURE_NUMBER_MEASURES)
            queueAccelerometerX.pop()
        if(queueAccelerometerY.size == Parameters.GESTURE_NUMBER_MEASURES)
            queueAccelerometerY.pop()
        if(queueAccelerometerZ.size == Parameters.GESTURE_NUMBER_MEASURES)
            queueAccelerometerZ.pop()

        queueAccelerometerX.add(accelerometer_x.toDouble())
        queueAccelerometerY.add(accelerometer_y.toDouble())
        queueAccelerometerZ.add(accelerometer_z.toDouble())
    }

    private fun recordDataOfGyroscope(gyroscope_x: Float, gyroscope_y: Float, gyroscope_z: Float) {
        if(queueGyroscopeX.size == Parameters.GESTURE_NUMBER_MEASURES)
            queueGyroscopeX.pop()
        if(queueGyroscopeY.size == Parameters.GESTURE_NUMBER_MEASURES)
            queueGyroscopeY.pop()
        if(queueGyroscopeZ.size == Parameters.GESTURE_NUMBER_MEASURES)
            queueGyroscopeZ.pop()

        queueGyroscopeX.add(gyroscope_x.toDouble())
        queueGyroscopeY.add(gyroscope_y.toDouble())
        queueGyroscopeZ.add(gyroscope_z.toDouble())
    }

}