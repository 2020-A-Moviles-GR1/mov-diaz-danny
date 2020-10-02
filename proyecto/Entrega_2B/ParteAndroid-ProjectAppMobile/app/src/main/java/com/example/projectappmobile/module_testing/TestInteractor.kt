package com.example.projectappmobile.module_testing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.TextView
import com.example.projectappmobile.R
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.MyFileManager
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.Parameters.Companion.THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY
import com.example.projectappmobile.models.neural_network.*
import com.example.projectappmobile.models.retrofit.ApiClient
import com.example.projectappmobile.models.retrofit.ApiInterface
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataFunction
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataString
import com.example.projectappmobile.models.sockets.SocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class TestInteractor(
    private val mContext: Context,
    private val callbackPresenter: TestContracts.Presenter,
    private val handler: Handler,
    private val sensorManager: SensorManager,
    private val linearAccelerometer: Sensor,
    private val gyroscope: Sensor,
    private val username_user: String
) : TestContracts.Interactor, SensorEventListener {


    lateinit var webSocket: WebSocket

    init {
        sensorManager.registerListener(this, gyroscope, Parameters.TIME_PER_GESTURE_MEASURE_MS)
        sensorManager.registerListener(this, linearAccelerometer, Parameters.TIME_PER_GESTURE_MEASURE_MS)
    }

    companion object {
        const val TAG: String = "TestInteractor";
    }

    private var waiting_for_gyroscope: Boolean = false
    private var waiting_for_accelerometer: Boolean = false
    private var accelerometer_cuted: Boolean = false
    private var gyroscope_cuted: Boolean = false
    private var once: Boolean = true
    private var is_streaming: Boolean = false
    private var queueAccelerometerX = LinkedList<Double>()
    private var queueAccelerometerY = LinkedList<Double>()
    private var queueAccelerometerZ = LinkedList<Double>()
    private var queueGyroscopeX = LinkedList<Double>()
    private var queueGyroscopeY = LinkedList<Double>()
    private var queueGyroscopeZ = LinkedList<Double>()


    private var queueAccelerometerXForPredict = LinkedList<Double>()
    private var queueAccelerometerYForPredict = LinkedList<Double>()
    private var queueAccelerometerZForPredict = LinkedList<Double>()
    private var queueGyroscopeXForPredict = LinkedList<Double>()
    private var queueGyroscopeYForPredict = LinkedList<Double>()
    private var queueGyroscopeZForPredict = LinkedList<Double>()



    private var recordData = false
    private var plotDataAccelerometer = false
    private var plotDataGyroscope = false


    private var neuralNetwork: NeuralNetwork = NeuralNetwork()




    private fun fusionLastWithNewData(lastSensorData: List<List<Double>>, sensorData: LinkedList<Double>): List<List<Double>>{
        val holderSensor = ArrayList<List<Double>>();
        holderSensor.addAll(lastSensorData)
        holderSensor.add(sensorData)
        return holderSensor
    }

    private fun isSetOfDataComplete(): Boolean{
        return queueAccelerometerX.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueAccelerometerY.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueAccelerometerZ.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueGyroscopeX.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueGyroscopeY.size == Parameters.GESTURE_NUMBER_MEASURES &&
                queueGyroscopeZ.size == Parameters.GESTURE_NUMBER_MEASURES
    }

    var is_playing_sound: Boolean = false
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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }

    override fun onSensorChanged(event: SensorEvent?) {

        val sensor:Sensor? = event?.sensor

        if(sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION){
            val accelerometer_x: Float = (event.values?.get(0) ?: Parameters.INVALID_SENSOR_VALUE)
            val accelerometer_y: Float = (event.values?.get(1) ?: Parameters.INVALID_SENSOR_VALUE)
            val accelerometer_z: Float = (event.values?.get(2) ?: Parameters.INVALID_SENSOR_VALUE)

            if(recordData && !waiting_for_gyroscope){
                if(queueAccelerometerX.size == Parameters.GESTURE_NUMBER_MEASURES)
                    queueAccelerometerX.pop()
                if(queueAccelerometerY.size == Parameters.GESTURE_NUMBER_MEASURES)
                    queueAccelerometerY.pop()
                if(queueAccelerometerZ.size == Parameters.GESTURE_NUMBER_MEASURES)
                    queueAccelerometerZ.pop()

                queueAccelerometerX.add(accelerometer_x.toDouble())
                queueAccelerometerY.add(accelerometer_y.toDouble())
                queueAccelerometerZ.add(accelerometer_z.toDouble())

                callbackPresenter.onGetDataAccelerometer(accelerometer_x, accelerometer_y, accelerometer_z, plotDataAccelerometer)
                plotDataAccelerometer = false

                val actual_size = queueAccelerometerZ.size

                if(is_streaming && actual_size >= Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY){



                    if(once){
                        if(isPosibleGesture(
                                LinkedList(queueAccelerometerX.subList(actual_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, actual_size)),
                                LinkedList(queueAccelerometerY.subList(actual_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, actual_size)),
                                LinkedList(queueAccelerometerZ.subList(actual_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, actual_size))
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
                            if(actual_size == Parameters.GESTURE_NUMBER_MEASURES){
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
                            if(actual_size >= THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY){
                                cutAccelerometerData()
                            }
                        }
                    }

                }
            }
        }
        else if(sensor?.type == Sensor.TYPE_GYROSCOPE){
            val gyroscope_x = (event.values?.get(0) ?: Parameters.INVALID_SENSOR_VALUE)
            val gyroscope_y = (event.values?.get(1) ?: Parameters.INVALID_SENSOR_VALUE)
            val gyroscope_z = (event.values?.get(2) ?: Parameters.INVALID_SENSOR_VALUE)

            if(recordData && !waiting_for_accelerometer) {
                if(queueGyroscopeX.size == Parameters.GESTURE_NUMBER_MEASURES)
                    queueGyroscopeX.pop()
                if(queueGyroscopeY.size == Parameters.GESTURE_NUMBER_MEASURES)
                    queueGyroscopeY.pop()
                if(queueGyroscopeZ.size == Parameters.GESTURE_NUMBER_MEASURES)
                    queueGyroscopeZ.pop()

                queueGyroscopeX.add(gyroscope_x.toDouble())
                queueGyroscopeY.add(gyroscope_y.toDouble())
                queueGyroscopeZ.add(gyroscope_z.toDouble())

                callbackPresenter.onGetDataGyroscope(gyroscope_x, gyroscope_y, gyroscope_z, plotDataGyroscope)
                plotDataGyroscope = false

                val actual_size = queueGyroscopeZ.size

                if(is_streaming && actual_size >= Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY){

                    if(once){
                        if(isPosibleGesture(
                                LinkedList(queueGyroscopeX.subList(actual_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, actual_size)),
                                LinkedList(queueGyroscopeY.subList(actual_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, actual_size)),
                                LinkedList(queueGyroscopeZ.subList(actual_size-Parameters.THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY, actual_size))
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
                            if(actual_size == Parameters.GESTURE_NUMBER_MEASURES){
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
                            if(actual_size >= THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY){
                                cutGyroscopeData()
                            }
                        }
                    }

                }

            }

        }
    }

    private fun cutAccelerometerData() {
        val len = queueAccelerometerZ.size
        val begin = len-THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY;
        val end = len

        queueAccelerometerX = LinkedList(queueAccelerometerX.subList(begin, end))
        queueAccelerometerY = LinkedList(queueAccelerometerY.subList(begin, end))
        queueAccelerometerZ = LinkedList(queueAccelerometerZ.subList(begin, end))

        println("CUT ACCELEROMETER -> SIZE: ${queueAccelerometerZ.size}")
        println("ACCELEROMETER:$queueAccelerometerX\n$queueAccelerometerY\n$queueAccelerometerZ\n")
        accelerometer_cuted = true

    }

    private fun cutGyroscopeData() {
        val len = queueGyroscopeZ.size
        val begin = len-THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY;
        val end = len

        queueGyroscopeX = LinkedList(queueGyroscopeX.subList(begin, end))
        queueGyroscopeY = LinkedList(queueGyroscopeY.subList(begin, end))
        queueGyroscopeZ = LinkedList(queueGyroscopeZ.subList(begin, end))

        println("CUT GYROSCOPE-> SIZE: ${queueGyroscopeZ.size}")
        println("GYROSCOPE:$queueGyroscopeX\n$queueGyroscopeY\n$queueGyroscopeZ\n")
        gyroscope_cuted = true
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


    private fun controlPlotData(checkedPlotAccelerometer: Boolean, checkedPlotGyroscope: Boolean) {
        if(checkedPlotAccelerometer){
            plotDataAccelerometer = true
            controlPlotAccelerometerData()
        }
        if(checkedPlotGyroscope) {
            plotDataGyroscope = true
            controlPlotGyroscopeData()
        }

    }

    private fun controlPlotAccelerometerData() {

        Thread(Runnable {
            while (recordData){
                plotDataAccelerometer = true
                try {
                    Thread.sleep((Parameters.TIME_PER_GESTURE_MEASURE_MS.toLong()))

                }
                catch (e: Exception) {
                    e.message?.let { Log.e(TAG, it) }
                }
            }
            plotDataAccelerometer = false
        }).start()
    }

    private fun controlPlotGyroscopeData() {
        Thread(Runnable {
            while (recordData){
                plotDataGyroscope = true
                try {
                    Thread.sleep((Parameters.TIME_PER_GESTURE_MEASURE_MS.toLong()))
                }
                catch (e: Exception) {
                    e.message?.let { Log.e(TAG, it) }
                }
            }
            plotDataGyroscope = false
        }).start()
    }

    private fun recoverNN(): Boolean {
        if(!neuralNetwork.is_trained){
            val last_nn = NeuralNetwork.recoverNeuralNetwork(mContext);
            if (last_nn != null){
                neuralNetwork = last_nn;
                return true
            }else {
                callbackPresenter.onRecoverNNFailed("No hay ninguna red entrenada todavía")
                return false
            }
        }
        return true
    }

    override fun createGesture(id_function: Int, username_user:String, name:String,
                               data_accelerometer_x: String, data_accelerometer_y: String, data_accelerometer_z: String,
                               data_gyroscope_x: String, data_gyroscope_y: String, data_gyroscope_z: String,
                               is_augmented_data: Boolean) {

        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )

        val map = HashMap<String, Any>()
        map["id_function"] = id_function
        map["username_user"] = username_user
        map["name"] = name



        val call: Call<MyServerResponseDataString>? = apiInterface?.createGesture(map)
        call?.enqueue(object : Callback<MyServerResponseDataString?> {
            override fun onResponse(
                call: Call<MyServerResponseDataString?>,
                responseDataString: Response<MyServerResponseDataString?>
            ) {
                Log.d(TAG, "onResponse: createGesture: " + responseDataString.body())

                if(responseDataString.body() != null){
                    TestActivity.gesture_created = true
                    createSamplesForGesture(
                        id_function, username_user,
                        data_accelerometer_x, data_accelerometer_y, data_accelerometer_z,
                        data_gyroscope_x, data_gyroscope_y, data_gyroscope_z,
                        is_augmented_data
                    )
                }

            }

            override fun onFailure(call: Call<MyServerResponseDataString?>, t: Throwable) {
                callbackPresenter.onCreateGestureFailed(ErrorHandler.euphemiseMessage(t))
                Log.e(TAG, "createGesture: $t")
            }
        })
    }



    override fun createSamplesForGesture(
        id_function: Int, username_user: String,
        data_accelerometer_x: String, data_accelerometer_y: String, data_accelerometer_z: String,
        data_gyroscope_x: String, data_gyroscope_y: String, data_gyroscope_z: String,
        is_augmented_data: Boolean
    ) {
        Log.i(TAG, "Enviando samples para el el gesto ($id_function, $username_user) y vectores de sensores. Muestra a_x $data_accelerometer_x")

        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )

        val map = HashMap<String, Any>()
        map["id_function"] = id_function
        map["username_user"] = username_user
        map["is_augmented_data"] = is_augmented_data
        map["data_accelerometer_x"] = data_accelerometer_x
        map["data_accelerometer_y"] = data_accelerometer_y
        map["data_accelerometer_z"] = data_accelerometer_z
        map["data_gyroscope_x"] = data_gyroscope_x
        map["data_gyroscope_y"] = data_gyroscope_y
        map["data_gyroscope_z"] = data_gyroscope_z


        val call: Call<MyServerResponseDataString>? = apiInterface?.uploadSampleForGesture(map)
        call?.enqueue(object : Callback<MyServerResponseDataString?> {
            override fun onResponse(
                call: Call<MyServerResponseDataString?>,
                responseDataString: Response<MyServerResponseDataString?>
            ) {

                Log.d(TAG, "onResponse: uploadSamplesForGesture: " + responseDataString.body())
                callbackPresenter.onCreateSamplesForGestureSuccessful()
            }

            override fun onFailure(call: Call<MyServerResponseDataString?>, t: Throwable) {
                callbackPresenter.onCreateSamplesForGestureFailed(ErrorHandler.euphemiseMessage(t))
                Log.e(TAG, "createSamplesForGesture: $t")
            }
        })
    }

    override fun getFunctions() {

        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )
        val call: Call<MyServerResponseDataFunction>? = apiInterface?.getFunctions(1)
        call?.enqueue(object : Callback<MyServerResponseDataFunction?> {
            override fun onResponse(
                call: Call<MyServerResponseDataFunction?>,
                response: Response<MyServerResponseDataFunction?>
            ) {

                Log.d(TAG, "onResponse: uploadSamplesForGesture: " + response.body())

                if(response.body() != null){
                    callbackPresenter.onGetFunctionsSuccessful(response.body()?.data)
                }else{
                    callbackPresenter.onGetFunctionsFailed("No hay funciones")
                }
            }

            override fun onFailure(call: Call<MyServerResponseDataFunction?>, t: Throwable) {
                callbackPresenter.onGetFunctionsFailed(ErrorHandler.euphemiseMessage(t))
                Log.e(TAG, "createSamplesForGesture: $t")
            }
        })
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

    override fun startRecordingData(checkPlotAccelerometer: Boolean, checkPlotGyroscope: Boolean) {
        resetLists()
        callbackPresenter.onClearLineChart(checkPlotAccelerometer, checkPlotGyroscope)


        Thread(Runnable {
            var time_ms = 0

            var counter = Parameters.WAIT_TIME_S

            handler.post{
                callbackPresenter.onUpdateTextViewResult("Inicio en: $counter")
            }

            while (counter > 0) {
                try {
                    Thread.sleep(1000)
                    counter -= 1
                    handler.post {
                        callbackPresenter.onUpdateTextViewResult("Inicio en: $counter")
                    }
                } catch (e: Exception) {
                    e.message?.let { Log.e(TAG, it) }
                }
            }

            handler.post {
                playSound(R.raw.beep)
            }

            recordData = true
            controlPlotData(checkPlotAccelerometer, checkPlotGyroscope)

            try {
                Thread.sleep(Parameters.GESTURE_DURATION_MS.toLong())
                // puede no ser completado de forma exacta
            } catch (e: Exception) {
                e.message?.let { Log.e(TAG, it) }
            }

            // barrera de seguridad por si tiene valores incompletos
            while (!isSetOfDataComplete()) {
                try {
                    time_ms += Parameters.TIME_PER_GESTURE_MEASURE_MS
                    // wait for one more sample
                } catch (e: Exception) {
                    e.message?.let { Log.e(TAG, it) }
                }
            }

            recordData = false

            Log.i(TAG, "Se recolectaron ${Parameters.GESTURE_NUMBER_MEASURES} ejemplares por eje de cada sensor")

            handler.post{
                callbackPresenter.onRecordingComplete()
                callbackPresenter.onUpdateTextViewResult("Medición terminada")
                playSound(R.raw.confirmation)
            }
        }).start()
    }

    override fun onResume() {
        val client = OkHttpClient()
        // opcional .addHeader("sec-websocket-protocol", "echo-protocol")
        val request: Request = Request.Builder().url(Parameters.WS_SERVER_PATH).addHeader("sec-websocket-protocol", "echo-protocol").build()

        webSocket = client.newWebSocket(request, SocketListener(
            mContext, handler, callbackPresenter)
        )
    }

    override fun onPause() {
        sensorManager.unregisterListener(this, linearAccelerometer)
        sensorManager.unregisterListener(this, gyroscope)
        webSocket.close(1001, "Detenido OnPause")
    }

    override fun onDestruct() {
        sensorManager.unregisterListener(this, linearAccelerometer)
        sensorManager.unregisterListener(this, gyroscope)
    }

    override fun onStop() {
        sensorManager.unregisterListener(this, linearAccelerometer)
        sensorManager.unregisterListener(this, gyroscope)
    }


    private fun mapForceUntilMax(max:Double=10.0){
        queueAccelerometerX = LinkedList(queueAccelerometerX.map { clampAbsolute(it, max) })
        queueAccelerometerY = LinkedList(queueAccelerometerY.map { clampAbsolute(it, max) })
        queueAccelerometerZ = LinkedList(queueAccelerometerZ.map { clampAbsolute(it, max) })
        queueGyroscopeX = LinkedList(queueGyroscopeX.map { clampAbsolute(it, max) })
        queueGyroscopeY = LinkedList(queueGyroscopeY.map { clampAbsolute(it, max) })
        queueGyroscopeZ = LinkedList(queueGyroscopeZ.map { clampAbsolute(it, max) })

    }

    override fun saveSampleLocalAndInDB(
        filename: String,
        username_user: String,
        id_function: Int
    ) {


        // mapForceUntilMax()



        val gestureFullData = readGestureData(mContext, filename, -999);

        if(gestureFullData.label == -1 || gestureFullData.accelerometerXs.isEmpty()){
            // el archivo no existe
            writeGestureData(mContext, filename,
                arrayListOf(queueAccelerometerX), arrayListOf(queueAccelerometerY), arrayListOf(queueAccelerometerZ),
                arrayListOf(queueGyroscopeX), arrayListOf(queueGyroscopeY), arrayListOf(queueGyroscopeZ))

        }
        else{
            // el archivo si existe, se debe anexar
            writeGestureData(mContext, filename,
                fusionLastWithNewData(gestureFullData.accelerometerXs, queueAccelerometerX),
                fusionLastWithNewData(gestureFullData.accelerometerYs, queueAccelerometerY),
                fusionLastWithNewData(gestureFullData.accelerometerZs, queueAccelerometerZ),
                fusionLastWithNewData(gestureFullData.gyroscopeXs, queueGyroscopeX),
                fusionLastWithNewData(gestureFullData.gyroscopeYs, queueGyroscopeY),
                fusionLastWithNewData(gestureFullData.gyroscopeZs, queueGyroscopeZ)
            )

        }

        val raw_id_functions: String? = MyFileManager.readFromFile(mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(raw_id_functions.equals("") || raw_id_functions == null){
            // archivo no existe
            MyFileManager.writeToFile(convertIntListToRawString(arrayListOf(id_function)), mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
            Log.i(TAG, "First Id_function stored: ${arrayListOf(id_function)}")

            callbackPresenter.onCreateGesture(id_function,  username_user, "Gesto test",
                queueAccelerometerX.toString(), queueAccelerometerY.toString(), queueAccelerometerZ.toString(),
                queueGyroscopeX.toString(), queueGyroscopeY.toString(), queueGyroscopeZ.toString())


        }else{
            val arraw_id_functions: ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            if(!arraw_id_functions.contains(id_function)){
                arraw_id_functions.add(id_function)

                callbackPresenter.onCreateGesture(id_function,  username_user, "Gesto test",
                    queueAccelerometerX.toString(), queueAccelerometerY.toString(), queueAccelerometerZ.toString(),
                    queueGyroscopeX.toString(), queueGyroscopeY.toString(), queueGyroscopeZ.toString()
                )
            }else{

                callbackPresenter.onCreateSamplesForGesture(
                    id_function, username_user,
                    queueAccelerometerX.toString(), queueAccelerometerY.toString(), queueAccelerometerZ.toString(),
                    queueGyroscopeX.toString(), queueGyroscopeY.toString(), queueGyroscopeZ.toString()
                )
            }
            Log.i(TAG, "Id_functions stored: $arraw_id_functions")
            MyFileManager.writeToFile(convertIntListToRawString(arraw_id_functions), mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        }

        callbackPresenter.onSavedLocally("Guardado localmente");
    }

    override fun resetLocalDataForFunction(username_user: String, id_function: Int) {
        val raw_id_functions: String? = MyFileManager.readFromFile(mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(!raw_id_functions.equals("") && raw_id_functions != null){
            val arraw_id_functions: ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            arraw_id_functions.remove(id_function)
            MyFileManager.writeToFile(convertIntListToRawString(arraw_id_functions), mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
            Log.i(TAG, "Updated: $arraw_id_functions")
            val filename = "data,$username_user,function$id_function.txt";
            MyFileManager.writeToFile("", mContext, filename)
            callbackPresenter.onResetLocalDataForFunctionSuccess("Updated functions stored")
        }
        else{
            callbackPresenter.onResetLocalDataForFunctionFailed("No hay funciones guardadas")
        }
    }

    override fun getResumeDataLocal(
        username_user: String,
        mapFuntionToName: HashMap<Int, Any>
    ) {
        val raw_id_functions: String? = MyFileManager.readFromFile(mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(!raw_id_functions.equals("") && raw_id_functions != null){
            val id_functions: ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))

            var output = ""
            for (i in 0..(id_functions.size-1)) {
                val id_function: Int = id_functions[i];
                val filename = "data,$username_user,function$id_function.txt";
                val gestureFullData = readGestureData(mContext, filename, i);
                output += "${mapFuntionToName[id_function]} -> ${gestureFullData.accelerometerXs.size} samples\n"

            }
            callbackPresenter.onGetResumeDataLocalSuccess(output)
        }
        else{
            callbackPresenter.onGetResumeDataLocalFailed("No hay funciones guardadas")
        }
    }

    override fun streamBegin(checkedPlotAccelerometer: Boolean, checkedPlotGyroscope: Boolean) {
        resetLists()
        callbackPresenter.onClearLineChart(checkedPlotAccelerometer, checkedPlotGyroscope)

        once = true
        recordData = true
        is_streaming = true
        controlPlotData(checkedPlotAccelerometer, checkedPlotGyroscope)
    }

    override fun streamStop() {
        recordData = false
        is_streaming = false
    }

    override fun trainModel(
        usernameUser: String,
        mapFuntionToName: java.util.HashMap<Int, Any>,
        config: NeuralNetwork.Config,
        textViewTestLogNN: TextView
    ) {

        val raw_id_functions: String? = MyFileManager.readFromFile(mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(raw_id_functions.equals("") || raw_id_functions == null){
            callbackPresenter.onTrainModelFailed("No se ha guardado ningun gesto aun en el teléfono")
            return
        }


        val ids_functions_gestures: List<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
        if(ids_functions_gestures.size < 2){
            callbackPresenter.onTrainModelFailed("Se requiere almenos dos gestos con una función diferente asociada al menos")
            return
        }

        neuralNetwork = NeuralNetwork(NeuralNetwork.Config(), false, callbackPresenter)
        neuralNetwork.initMapClassToFunction(ids_functions_gestures)
        neuralNetwork.mapFuntionToName = mapFuntionToName

        neuralNetwork.addLog("Id functions to train: $ids_functions_gestures")
        neuralNetwork.addLog("mapClassToFunction: ${neuralNetwork.mapClassToFunction}")
        neuralNetwork.addLog("mapFuntionToName: ${neuralNetwork.mapFuntionToName}")


        val (X, y, log) = getData(mContext, neuralNetwork.config.augmentation_by_shifting_percent_of_data,
            neuralNetwork.config.samples,Parameters.GESTURE_NUMBER_MEASURES,neuralNetwork.config.normalize,
            neuralNetwork.config.augmentation_by_move_average_box_begin,
            neuralNetwork.config.augmentation_by_move_average_box_end, usernameUser,
            neuralNetwork.config.shuffle,neuralNetwork.config.random_seed_shuffle, ids_functions_gestures, neuralNetwork.config.verbose
        );

        neuralNetwork.addLog(log)


        val (X_y_train, X_y_test, X_y_validation) = split_train_test_validation(
            X, y, neuralNetwork.config.train_split_portion, neuralNetwork.config.validation_split_portion
        );
        val(X_train, y_train) = X_y_train;
        val(X_test, y_test) = X_y_test;
        val(X_validation, y_validation) = X_y_validation;

        neuralNetwork.addLog("X_train: ${X_train.size}, y_train: ${y_train.size}")
        neuralNetwork.addLog("X_test: ${X_test.size}, y_test: ${y_test.size}")
        neuralNetwork.addLog("X_val: ${X_validation.size}, y_validation: ${y_validation.size}")



        val number_features = X[0].size;  // número de caracteristicas, en este caso son 600
        val number_clases = ids_functions_gestures.size;  // número de clases

        neuralNetwork.config.neurons_per_layer[0] = number_features;
        neuralNetwork.config.neurons_per_layer[neuralNetwork.config.neurons_per_layer.size-1] = number_clases;

        neuralNetwork.addLog("Neurons architecture: " + convertIntArrayToRawString(neuralNetwork.config.neurons_per_layer))


        // los pesos solo son un vector enrrollado
        neuralNetwork.initWeights();


        neuralNetwork.addLog("Beginning training")
        handler.post{
            callbackPresenter.onUpdateTextViewLogNN(neuralNetwork.getLog())
        }

        val time_begin = System.currentTimeMillis()
        val (Js_train: List<Double>, Js_validation: List<Double>) = neuralNetwork.train(
            X_train, y_train, X_validation, y_validation
        );
        val time_end = System.currentTimeMillis()
        val time_seconds = (time_end-time_begin)/1000;

        neuralNetwork.addLog("End training in $time_seconds seconds")
        val J_test: Double = neuralNetwork.costFunction(X_test, y_test);

        neuralNetwork.addLog("Js_train: $Js_train")
        neuralNetwork.addLog("Js_validation: $Js_validation")
        neuralNetwork.addLog("Error final de test: $J_test")



        val (predictions_train, logits_train) = neuralNetwork.predict(X_train);
        val (predictions_test, logits_test) = neuralNetwork.predict(X_test);

        var sum_accuracy_train = 0.0;
        for(i in 0..(y_train.size-1)){
            val pred = predictions_train[i][0];
            val real = y_train[i][0];
            if(pred == real){
                sum_accuracy_train += 1;
            }
        }
        val accuracy_train: Double = (sum_accuracy_train/y_train.size) * 100;

        var sum_accuracy_test = 0.0;
        for(i in 0..(y_test.size-1)){
            if(predictions_test[i][0] == y_test[i][0]){
                sum_accuracy_test += 1.0;
            }
        }
        val accuracy_test = (sum_accuracy_test/y_test.size) * 100f;

        neuralNetwork.addLog("Accuracy: Train: $accuracy_train%, Test: $accuracy_test%")

        handler.post {
            callbackPresenter.onUpdateTextViewLogNN(neuralNetwork.getLog())
        }

        neuralNetwork.is_trained = true
        neuralNetwork.accuracy_train = accuracy_train
        neuralNetwork.accuracy_test = accuracy_test

        handler.post{
            callbackPresenter.onTrainedModel("Entrenado")
        }

    }



    override fun predictOne() {

        if(!recoverNN()){
            return
        }

        if(queueGyroscopeZ.size == 0){
            callbackPresenter.onPredictFailed("Se debe guardar un dato de sensor primero")
            return
        }
        val X = ArrayList<List<Double>>()
        X.add(fusionVectorByHorizontal(
            queueAccelerometerX, queueAccelerometerY, queueAccelerometerZ,
            queueGyroscopeX, queueGyroscopeY, queueGyroscopeZ
        ))



        val (predict_class, logits) = neuralNetwork.predict(X);
        Log.i(TAG, "Class: $predict_class, logits: $logits")
        val predict_function = neuralNetwork.mapClassToFunction.get(predict_class[0][0]).toString().toInt()
        val predict_name = neuralNetwork.mapFuntionToName.get(predict_function)
        callbackPresenter.onPredictSuccess("class: $predict_class, function: $predict_function\nPred: ${predict_name}")
    }


    fun predictFromStream() {

        if(!recoverNN()){
            return
        }

        if(queueAccelerometerZForPredict.size == 0 || queueGyroscopeZForPredict.size == 0){
            println("Un gesto debe ser reconocido antes, tiene datos vacios")
            // callbackPresenter.onPredictFailed("Un gesto debe ser reconocido antes, tiene datos vacios")
            return
        }
        val X = ArrayList<List<Double>>()
        X.add(fusionVectorByHorizontal(
            queueAccelerometerXForPredict, queueAccelerometerYForPredict, queueAccelerometerZForPredict,
            queueGyroscopeXForPredict, queueGyroscopeYForPredict, queueGyroscopeZForPredict
        ))



        val (predict_class, logits) = neuralNetwork.predict(X);
        Log.i(TAG, "Class: $predict_class, logits: $logits")
        val predict_function: Int = neuralNetwork.mapClassToFunction.get(predict_class[0][0]).toString().toInt()
        val predict_name = neuralNetwork.mapFuntionToName.get(predict_function)
        callbackPresenter.onPredictSuccess("class: $predict_class, function: $predict_function\nPred: ${predict_name}")


        queueAccelerometerXForPredict = LinkedList()
        queueAccelerometerYForPredict = LinkedList()
        queueAccelerometerZForPredict = LinkedList()

        queueGyroscopeXForPredict = LinkedList()
        queueGyroscopeYForPredict = LinkedList()
        queueGyroscopeZForPredict = LinkedList()


        streamStop()
        waiting_for_gyroscope = false
        waiting_for_accelerometer = false
        once = true
        gyroscope_cuted = false
        accelerometer_cuted = false


        socketSendMessage(predict_function, "$username_user-$predict_name")

    }

    private fun socketSendMessage(predictFunction: Int, data: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("id", predictFunction)
            jsonObject.put("data", data)
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
        } catch (e: JSONException) {
            println("Error $e")
            callbackPresenter.onPredictFailed("Error al predecir")
        }
    }

    override fun getInfoNN(): String {
        if(!recoverNN()){
            return ""
        }

        if(neuralNetwork.is_trained && neuralNetwork.mapFuntionToName.size > 0){

            var output = "${neuralNetwork.mapClassToFunction.size} functions\n";
            for(i in 0..(neuralNetwork.mapClassToFunction.size-1)){
                val id_function:Int = neuralNetwork.mapClassToFunction.get(i).toString().toInt()
                output += "| ${id_function} -> ${neuralNetwork.mapFuntionToName.get(id_function)} |\n"
            }

            output += "\n\nACCTrain: ${neuralNetwork.accuracy_train}\nACCTest: ${neuralNetwork.accuracy_test}"

            return output

        }else{
            return "No Neural network trained"
        }
    }

    override fun saveModel() {
        if(neuralNetwork.is_trained){
            Thread(Runnable {
                neuralNetwork.addLog("Saving model")
                val time_begin = System.currentTimeMillis()
                MyFileManager.writeToFile(neuralNetwork.saveToString(neuralNetwork.accuracy_train, neuralNetwork.accuracy_test), mContext, Parameters.FILENAME_MY_NN)
                val time_end = System.currentTimeMillis()
                val time_seconds = (time_end-time_begin)/1000;
                neuralNetwork.addLog("Model saved in $time_seconds seconds")


                handler.post{
                    callbackPresenter.onSaveModeSuccess("Model SAVED in $time_seconds seconds")
                }

            }).start()

        }else{
           callbackPresenter.onSaveModelFailed("No hay una red neuronal entrenada")
        }

    }

    override fun resetAll(username_user: String) {
        val raw_id_functions: String? = MyFileManager.readFromFile(mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(!raw_id_functions.equals("") && raw_id_functions != null){
            val arraw_id_functions: ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            for (i in 0..(arraw_id_functions.size-1)) {
                val id_function = arraw_id_functions.get(i);
                val filename = "data,$username_user,function$id_function.txt";
                MyFileManager.writeToFile("", mContext, filename)
            }
            NeuralNetwork.eraseSavedData(mContext)
            MyFileManager.writeToFile("", mContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        }
        callbackPresenter.onResetAllSuccess("Cleaned")
    }


}