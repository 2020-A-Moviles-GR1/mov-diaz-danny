package com.example.projectappmobile.module_gestures.interactor

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.MyFileManager
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.models.neural_network.*
import com.example.projectappmobile.models.retrofit.ApiClient
import com.example.projectappmobile.models.retrofit.ApiInterface
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataFunction
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataString
import com.example.projectappmobile.module_gestures.MyGesturesActivity
import com.example.projectappmobile.module_gestures.contracts.AddGestureContracts
import com.example.projectappmobile.module_testing.TestInteractor
import kotlinx.android.synthetic.main.activity_my_gestures.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddGestureInteractor  (
    private val context: Context,
    private val callBackPresenter: AddGestureContracts.Presenter,
    private val handler: Handler
): AddGestureContracts.Interactor{

    companion object {
        val TAG = "AddGestureContracts"
    }

    var samples = ArrayList<HashMap<String, String>>()
    var samples_array_accelerometerX = ArrayList<List<Double>>()
    var samples_array_accelerometerY = ArrayList<List<Double>>()
    var samples_array_accelerometerZ = ArrayList<List<Double>>()
    var samples_array_gyroscopeX = ArrayList<List<Double>>()
    var samples_array_gyroscopeY = ArrayList<List<Double>>()
    var samples_array_gyroscopeZ = ArrayList<List<Double>>()

    init {
        samples = ArrayList()
        samples_array_accelerometerX = ArrayList<List<Double>>()
        samples_array_accelerometerY = ArrayList<List<Double>>()
        samples_array_accelerometerZ = ArrayList<List<Double>>()
        samples_array_gyroscopeX = ArrayList<List<Double>>()
        samples_array_gyroscopeY = ArrayList<List<Double>>()
        samples_array_gyroscopeZ = ArrayList<List<Double>>()
    }


    override fun getFunctions() {
        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )
        val call: Call<MyServerResponseDataFunction>? = apiInterface?.getFunctions()
        call?.enqueue(object : Callback<MyServerResponseDataFunction?> {
            override fun onResponse(
                call: Call<MyServerResponseDataFunction?>,
                response: Response<MyServerResponseDataFunction?>
            ) {

                Log.d(MyGesturesActivity.TAG, "onResponse: getFunctions: " + response.body())

                if(response.body() != null){
                    val functions = response.body()?.data
                    functions?.let { callBackPresenter.onGetFunctionsSuccess(it) }
                }else{
                    callBackPresenter.onShowGeneralShortMessage("No hay funciones")
                }
            }

            override fun onFailure(call: Call<MyServerResponseDataFunction?>, t: Throwable) {
                callBackPresenter.onShowGeneralShortMessage(ErrorHandler.euphemiseMessage(t))
                Log.e(MyGesturesActivity.TAG, "getFunctions: $t")
            }
        })
    }

    override fun saveLocalSample(
        queueAccX: LinkedList<Double>,
        queueAccY: LinkedList<Double>,
        queueAccZ: LinkedList<Double>,
        queueGyrX: LinkedList<Double>,
        queueGyrY: LinkedList<Double>,
        queueGyrZ: LinkedList<Double>
    ) {
        val minimap = HashMap<String, String>()
        minimap.put("data_accelerometer_x", convertLinkedListDoubleToRawString(queueAccX))
        minimap.put("data_accelerometer_y", convertLinkedListDoubleToRawString(queueAccY))
        minimap.put("data_accelerometer_z", convertLinkedListDoubleToRawString(queueAccZ))
        minimap.put("data_gyroscope_x", convertLinkedListDoubleToRawString(queueGyrX))
        minimap.put("data_gyroscope_y", convertLinkedListDoubleToRawString(queueGyrY))
        minimap.put("data_gyroscope_z", convertLinkedListDoubleToRawString(queueGyrZ))

        samples.add(minimap)
        samples_array_accelerometerX.add(ArrayList(queueAccX))
        samples_array_accelerometerY.add(ArrayList(queueAccY))
        samples_array_accelerometerZ.add(ArrayList(queueAccZ))
        samples_array_gyroscopeX.add(ArrayList(queueGyrX))
        samples_array_gyroscopeY.add(ArrayList(queueGyrY))
        samples_array_gyroscopeZ.add(ArrayList(queueGyrZ))
        callBackPresenter.onSaveLocalSampleFinished()

    }

    override fun commitGestureInDB(id_function: Int, username_user: String, name: String) {
        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )

        // mapForceUntilMax()  todo: verificar si el recorde de fuerza mejora la precisión
        val map = HashMap<String, Any>()
        map.put("id_function", id_function)
        map.put("username_user", username_user)
        map.put("name", name)
        map.put("samples", samples)

        Log.d(TAG, map.get("samples").toString())

        val call: Call<MyServerResponseDataString>? = apiInterface?.insertGestureWithSamples(map)
        call?.enqueue(object : Callback<MyServerResponseDataString?> {
            override fun onResponse(
                call: Call<MyServerResponseDataString?>,
                response: Response<MyServerResponseDataString?>
            ) {

                Log.d(MyGesturesActivity.TAG, "onResponse: commitGestureInDB: " + response.body())

                if(response.body() != null){
                    if(response.body()!!.id_message == 1){
                        callBackPresenter.onCommitDBSuccess()
                    }else{
                        callBackPresenter.onCommitDBFailed("No se pudo subir a la base de datos")
                    }
                }else{
                    callBackPresenter.onCommitDBFailed("No hay funciones")
                }
            }

            override fun onFailure(call: Call<MyServerResponseDataString?>, t: Throwable) {
                callBackPresenter.onCommitDBFailed(ErrorHandler.euphemiseMessage(t))
                Log.e(MyGesturesActivity.TAG, "getFunctions: $t")
            }
        })
    }

    override fun commitGestureLocally(id_function: Int, username_user: String, name: String) {

        // mapForceUntilMax()  todo: verificar si el recorde de fuerza mejora la precisión
        val filename = "data,$username_user,function$id_function.txt";

        writeGestureData(context, filename,
            samples_array_accelerometerX, samples_array_accelerometerY, samples_array_accelerometerZ,
            samples_array_gyroscopeX, samples_array_gyroscopeY, samples_array_gyroscopeZ
        )

        val raw_id_functions: String? = MyFileManager.readFromFile(context, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(raw_id_functions.equals("") || raw_id_functions == null){
            // archivo no existe
            MyFileManager.writeToFile(convertIntListToRawString(arrayListOf(id_function)), context, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
            Log.i(TAG, "First Id_function stored: ${arrayListOf(id_function)}")
        }else{
            val arraw_id_functions: java.util.ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            if(!arraw_id_functions.contains(id_function)){
                arraw_id_functions.add(id_function)
            }
            Log.i(TAG, "Id_functions stored: $arraw_id_functions")
            MyFileManager.writeToFile(convertIntListToRawString(arraw_id_functions), context, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        }

        callBackPresenter.onCommitLocallyFinished();
    }

    override fun trainModel() {

        Thread(Runnable {
            val raw_id_functions: String? = MyFileManager.readFromFile(context, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
            if(raw_id_functions.equals("") || raw_id_functions == null){
                callBackPresenter.onShowGeneralShortMessage("No se ha guardado ningun gesto aun en el teléfono")
                return@Runnable
            }


            val ids_functions_gestures: List<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            if(ids_functions_gestures.size < 2){
                handler.post {

                    samples = ArrayList()
                    samples_array_accelerometerX = ArrayList<List<Double>>()
                    samples_array_accelerometerY = ArrayList<List<Double>>()
                    samples_array_accelerometerZ = ArrayList<List<Double>>()
                    samples_array_gyroscopeX = ArrayList<List<Double>>()
                    samples_array_gyroscopeY = ArrayList<List<Double>>()
                    samples_array_gyroscopeZ = ArrayList<List<Double>>()
                    callBackPresenter.onShowGeneralShortMessage("Se requiere almenos dos gestos con una función diferente asociada al menos para entrenar un modelo. Ingrese ejemplos para otro")

                }
                return@Runnable
            }

            val neuralNetwork = NeuralNetwork(NeuralNetwork.Config(), false, callBackPresenter)  // todo: obtener config de shared preferences si no se envía ninguna
            neuralNetwork.initMapClassToFunction(ids_functions_gestures)
            neuralNetwork.mapFuntionToName = Function.mapFunctionToName

            neuralNetwork.addLog("Id functions to train: $ids_functions_gestures")
            neuralNetwork.addLog("mapClassToFunction: ${neuralNetwork.mapClassToFunction}")
            neuralNetwork.addLog("mapFuntionToName: ${neuralNetwork.mapFuntionToName}")


            val (X, y, log) = getData(context, neuralNetwork.config.augmentation_by_shifting_percent_of_data,
                neuralNetwork.config.samples,Parameters.GESTURE_NUMBER_MEASURES,neuralNetwork.config.normalize,
                neuralNetwork.config.augmentation_by_move_average_box_begin,
                neuralNetwork.config.augmentation_by_move_average_box_end, User.loggedUser!!.username,
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

            callBackPresenter.onUpdateCompleteLog(neuralNetwork.getLog())

            var time_begin = System.currentTimeMillis()
            val (Js_train: List<Double>, Js_validation: List<Double>) = neuralNetwork.train(
                X_train, y_train, X_validation, y_validation
            );
            var time_end = System.currentTimeMillis()
            var time_seconds = (time_end-time_begin)/1000;

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

            callBackPresenter.onUpdateCompleteLog(neuralNetwork.getLog())

            neuralNetwork.is_trained = true
            neuralNetwork.accuracy_train = accuracy_train
            neuralNetwork.accuracy_test = accuracy_test



            neuralNetwork.addLog("Saving model")
            time_begin = System.currentTimeMillis()
            MyFileManager.writeToFile(neuralNetwork.saveToString(neuralNetwork.accuracy_train, neuralNetwork.accuracy_test), context, Parameters.FILENAME_MY_NN)
            time_end = System.currentTimeMillis()
            time_seconds = (time_end-time_begin)/1000;
            neuralNetwork.addLog("Model saved in $time_seconds seconds")


            handler.post {

                callBackPresenter.onTrainedModel()
            }
        }).start()




    }

    override fun onResume() {

    }

    override fun onPause() {

    }

}