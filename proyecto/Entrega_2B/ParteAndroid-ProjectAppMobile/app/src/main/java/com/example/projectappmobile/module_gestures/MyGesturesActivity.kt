package com.example.projectappmobile.module_gestures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.R
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.MyFileManager
import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.adapters.RecyclerViewGesturesAdapter
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.database.Gesture
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.models.neural_network.*
import com.example.projectappmobile.models.retrofit.ApiClient
import com.example.projectappmobile.models.retrofit.ApiInterface
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataFunction
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataGesture
import com.example.projectappmobile.module_gestures.view.AddGestureActivity
import com.example.projectappmobile.module_gestures.view.UpdateGestureActivity
import com.example.projectappmobile.module_testing.TestInteractor
import kotlinx.android.synthetic.main.activity_my_gestures.*
import kotlinx.android.synthetic.main.activity_my_gestures.progressBar
import kotlinx.android.synthetic.main.activity_test.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyGesturesActivity : AppCompatActivity(),
    RecyclerViewGesturesAdapter.RecyclerViewGesturesAdapterInterface,
NeuralNetwork.NeuralNetworkInterface{

    companion object {
        val TAG = "MyGesturesActivity"
    }


    val spinnerMap = HashMap<Int, Int>()

    lateinit var adaptador: RecyclerViewGesturesAdapter

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_gestures)

        handler = Handler(Looper.getMainLooper())

        buttonMyGestureAddGesture.setOnClickListener {
            startActivity(Intent(this, AddGestureActivity::class.java))
        }

        buttonMyGestureBack.setOnClickListener {
            super.onBackPressed()
        }


        textViewMyGesturesMessages.setMovementMethod(ScrollingMovementMethod());

    }

    override fun onResume() {
        super.onResume()
        initializeRecyclerView(this)
    }


    fun initializeRecyclerView(presenter: RecyclerViewGesturesAdapter.RecyclerViewGesturesAdapterInterface ) {
        progressBar.visibility = View.VISIBLE
        getFunctions(presenter)
    }

    private fun getFunctions(presenter: RecyclerViewGesturesAdapter.RecyclerViewGesturesAdapterInterface) {

        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )
        val call: Call<MyServerResponseDataFunction>? = apiInterface?.getFunctions()
        call?.enqueue(object : Callback<MyServerResponseDataFunction?> {
            override fun onResponse(
                call: Call<MyServerResponseDataFunction?>,
                response: Response<MyServerResponseDataFunction?>
            ) {

                Log.d(TAG, "onResponse: getFunctions: " + response.body())

                if(response.body() != null){
                    val functions = response.body()?.data

                    val spinnerArray = arrayListOf<String?>()
                    Function.mapFunctionToName = HashMap()
                    Function.mapFunctionToDescription = HashMap()

                    if (functions != null) {
                        for (i in 0 until functions.size) {
                            spinnerMap.put(i, functions.get(i).id)
                            spinnerArray.add(functions.get(i).name)

                            Function.mapFunctionToName.put(functions.get(i).id, functions.get(i).name)
                            functions.get(i).description?.let {
                                Function.mapFunctionToDescription.put(functions.get(i).id,
                                    it
                                )
                            }
                        }

                        getMyGestures(spinnerArray, presenter)



                    }else{
                        Log.e(TAG, "Error, functions null")
                    }
                }else{
                    GlobalMethods.showShortToast(baseContext, "No hay funciones")
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<MyServerResponseDataFunction?>, t: Throwable) {
                progressBar.visibility = View.GONE
                GlobalMethods.showLongToast(baseContext, ErrorHandler.euphemiseMessage(t))
                Log.e(TAG, "getFunctions: $t")
            }
        })
    }

    private fun getMyGestures(
        spinnerArray: ArrayList<String?>,
        presenter: RecyclerViewGesturesAdapter.RecyclerViewGesturesAdapterInterface
    ) {

        val username = User.getLoggedIngUser()?.username

        if(username == null){
            GlobalMethods.showShortToast(this, "No existe un usuario logeado")
            return
        }
        progressBar.visibility = View.VISIBLE

        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )

        val map = HashMap<String, Any>()
        map.put("username_user", username)
        map.put("option", 3)
        val call: Call<MyServerResponseDataGesture>? = apiInterface?.getMyGestures(map)

        call?.enqueue(object : Callback<MyServerResponseDataGesture?> {
            override fun onResponse(
                call: Call<MyServerResponseDataGesture?>,
                response: Response<MyServerResponseDataGesture?>
            ) {

                Log.d(TAG, "onResponse: getMyGestures: " + response.body())

                if(response.body() != null){
                    val gestures: List<Gesture>? = response.body()?.data


                    if (gestures != null) {

                        Log.i(TAG, Function.mapFunctionToName.toString())

                        adaptador = RecyclerViewGesturesAdapter(
                            baseContext, ArrayList(gestures), spinnerArray, presenter
                        )

                        recyclerViewMyGestures.adapter = adaptador
                        recyclerViewMyGestures.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                        recyclerViewMyGestures.itemAnimator = DefaultItemAnimator()

                        adaptador.notifyDataSetChanged()

                    }else{
                        Log.e(TAG, "Error, gestures null")
                    }
                }else{
                    GlobalMethods.showShortToast(baseContext, "No hay funciones")
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<MyServerResponseDataGesture?>, t: Throwable) {
                progressBar.visibility = View.GONE
                GlobalMethods.showLongToast(baseContext, ErrorHandler.euphemiseMessage(t))
                Log.e(TAG, "getMyGestures: $t")
            }
        })

    }

    override fun onEdit(gesture: Gesture, position: Int) {
        val intent = Intent(this, UpdateGestureActivity::class.java)
        intent.putExtra("name", gesture.name)
        intent.putExtra("id_function", gesture.id_function)
        startActivity(intent)
    }

    override fun onDelete(gesture: Gesture, position: Int) {
        val username = User.getLoggedIngUser()?.username

        if(username == null){
            GlobalMethods.showShortToast(this, "No existe un usuario logeado")
            return
        }
        progressBar.visibility = View.VISIBLE

        val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
            ApiInterface::class.java
        )

        val map = HashMap<String, Any>()
        map.put("username_user", username)
        map.put("id_function", gesture.id_function)

        val call: Call<MyServerResponseDataGesture>? = apiInterface?.deleteGesture(map)

        progressBar.visibility = View.VISIBLE

        call?.enqueue(object : Callback<MyServerResponseDataGesture?> {
            override fun onResponse(
                call: Call<MyServerResponseDataGesture?>,
                response: Response<MyServerResponseDataGesture?>
            ) {

                Log.d(TAG, "onResponse: onDelete: " + response.body())

                if(response.body() != null){
                    val gestures: List<Gesture>? = response.body()?.data

                    if (gestures != null) {
                        adaptador.removeGesture(position)
                        adaptador.notifyDataSetChanged()
                        resetLocalDataForFunction(username, gesture.id_function)
                        retrainModel()
                    }else{
                        Log.e(TAG, "Error, gesture deleted null")
                    }
                }else{
                    GlobalMethods.showShortToast(baseContext, "No hay gestos borrados")
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<MyServerResponseDataGesture?>, t: Throwable) {
                progressBar.visibility = View.GONE
                GlobalMethods.showLongToast(baseContext, ErrorHandler.euphemiseMessage(t))
                Log.e(TAG, "getMyGestures: $t")
            }
        })
    }

    private fun retrainModel() {
        progressBar.visibility = View.VISIBLE
        Thread(Runnable {
            val raw_id_functions: String? = MyFileManager.readFromFile(baseContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
            if(raw_id_functions.equals("") || raw_id_functions == null){
                handler.post {
                    GlobalMethods.showLongToast(baseContext, "No se ha guardado ningun gesto aun en el teléfono")

                }
                return@Runnable
            }


            val ids_functions_gestures: List<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            if(ids_functions_gestures.size < 2 ){
                handler.post {
                    GlobalMethods.showLongToast(
                        baseContext,
                        "Se requiere almenos dos gestos con una función diferente asociada al menos para entrenar un modelo. Ingrese ejemplos para otro"
                    )
                }
                return@Runnable
            }

            val neuralNetwork = NeuralNetwork(NeuralNetwork.Config(), false, this)  // todo: obtener config de shared preferences si no se envía ninguna
            neuralNetwork.initMapClassToFunction(ids_functions_gestures)
            neuralNetwork.mapFuntionToName = Function.mapFunctionToName

            neuralNetwork.addLog("Id functions to train: $ids_functions_gestures")
            neuralNetwork.addLog("mapClassToFunction: ${neuralNetwork.mapClassToFunction}")
            neuralNetwork.addLog("mapFuntionToName: ${neuralNetwork.mapFuntionToName}")


            val (X, y, log) = getData(baseContext, neuralNetwork.config.augmentation_by_shifting_percent_of_data,
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

            this.onUpdateCompleteLog(neuralNetwork.getLog())

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

            this.onUpdateCompleteLog(neuralNetwork.getLog())

            neuralNetwork.is_trained = true
            neuralNetwork.accuracy_train = accuracy_train
            neuralNetwork.accuracy_test = accuracy_test



            neuralNetwork.addLog("Saving model")
            time_begin = System.currentTimeMillis()
            MyFileManager.writeToFile(neuralNetwork.saveToString(neuralNetwork.accuracy_train, neuralNetwork.accuracy_test), baseContext, Parameters.FILENAME_MY_NN)
            time_end = System.currentTimeMillis()
            time_seconds = (time_end-time_begin)/1000;
            neuralNetwork.addLog("Model saved in $time_seconds seconds")

            handler.post {
                progressBar.visibility = View.GONE
            }

        }).start()
    }

    override fun onSpinnerItemClickListener(gesture: Gesture, i: Int, l: Long) {
        GlobalMethods.showShortToast(this, "Gesture: $gesture, i: $i, l: $l, name: ${Function.mapFunctionToName[spinnerMap[i]]}")
    }

    private fun resetLocalDataForFunction(username_user: String, id_function: Int) {
        val raw_id_functions: String? = MyFileManager.readFromFile(baseContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
        if(!raw_id_functions.equals("") && raw_id_functions != null){
            val arraw_id_functions: ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            arraw_id_functions.remove(id_function)
            MyFileManager.writeToFile(convertIntListToRawString(arraw_id_functions), baseContext, Parameters.FILENAME_MY_ID_FUNCTIONS_SAVED)
            Log.i(TestInteractor.TAG, "Updated: $arraw_id_functions")
            val filename = "data,$username_user,function$id_function.txt";
            MyFileManager.writeToFile("", baseContext, filename)
        }
    }

    override fun onEpochBegin(epoch: Int, msg: String) {

    }

    override fun onEpochPartFinished(actual_m: Int, msg: String) {

    }

    override fun onUpdateCompleteLog(log: String) {
        handler.post {
            textViewMyGesturesMessages.text = log

        }
    }


}