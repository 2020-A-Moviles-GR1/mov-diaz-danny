@file:Suppress("DEPRECATION")

package com.example.projectappmobile

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.projectappmobile.models.MyFileManager

import com.example.projectappmobile.models.neural_network.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.ln

@RunWith(RobolectricTestRunner::class)
class NeuralNetworkUnitTest {

    lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        writeMatrix()
    }

    @Test
    fun shuffleSameOrder(){

        val X = ArrayList<List<Double>>();
        val y = ArrayList<List<Int>>();
        X.add(arrayListOf(1.0,  2.0,  3.0))
        X.add(arrayListOf(4.0,  5.0,  6.0))
        X.add(arrayListOf(7.0,  8.0,  9.0))
        X.add(arrayListOf(10.0,  11.0,  12.0))
        X.add(arrayListOf(13.0,  14.0,  15.0))

        y.add(List(1){1})
        y.add(List(1){2})
        y.add(List(1){3})
        y.add(List(1){-15})
        y.add(List(1){-30})

        val seed:Long = 15;

        println("X: $X, y: $y")
        X.shuffle(Random(seed))
        y.shuffle(Random(seed))

        println("X: $X, y: $y")




    }

    @Test
    fun moveAverageBoxVectorNN(){

        val vector = arrayListOf(10.0,  20.0,  30.0,  10.0,  20.0,  30.0,  10.0,  20.0,  30.0,  10.0);

        val moved_not_pair = moveAverageBoxVector(vector, 5)
        assertEquals(arrayListOf(0.0,  0.0,  18.0,  22.0,  20.0,  18.0,  22.0,  20.0,  0.0,  0.0), moved_not_pair)

        val moved_not_pair2 = moveAverageBoxVector(vector, 3)
        assertEquals(arrayListOf(0.0,  20.0,  20.0,  20.0,  20.0,  20.0,  20.0,  20.0,  20.0,  0.0), moved_not_pair2)


        val moved_pair = moveAverageBoxVector(vector, 6)
        assertEquals(arrayListOf(0.0,  0.0,  15.0,  18.333333333333332, 16.666666666666668, 15.0,  18.333333333333332, 16.666666666666668, 0.0, 0.0), moved_pair)

        val moved_pair2 = moveAverageBoxVector(vector, 2)
        assertEquals(arrayListOf(5.0,  10.0,  15.0,  5.0,  10.0,  15.0,  5.0,  10.0,  15.0,  5.0), moved_pair2)


    }

    @Test
    fun fusionVectorByHorizontalNN() {

        assertEquals(arrayListOf(1.0,  2.0,  3.0,  4.0,  5.0,  6.0), fusionVectorByHorizontal(arrayListOf(1.0,  2.0,  3.0), arrayListOf(4.0,  5.0,  6.0)))
    }

    @Test
    fun shiftVectorNN() {

        val shift_left: List<Double> = shiftVector(arrayListOf(1.0,  2.0,  3.0,  4.0,  5.0,  6.0,  7.0,  8.0,  9.0,  10.0), 5, 0);
        print(shift_left)
        assertEquals(arrayListOf(0.0,  0.0,  0.0,  0.0,  0.0,  1.0,  2.0,  3.0,  4.0,  5.0), shift_left)

        val shift_right: List<Double> = shiftVector(arrayListOf(1.0,  2.0,  3.0,  4.0,  5.0,  6.0,  7.0,  8.0,  9.0,  10.0), 5, 1);
        print(shift_right)
        assertEquals(arrayListOf(6.0,  7.0,  8.0,  9.0,  10.0,  0.0,  0.0,  0.0,  0.0,  0.0), shift_right)

    }

    @Test
    fun getDataForNN(){

        // solo para el test
        writeMatrix()
        val (X, y) = getData(context, 0.1,
            10,
            100,
            false,
            2,
            3,
            "user_test",
            true,
            42,
            arrayListOf(15)
        );

        println(X);
        println(y);



    }

    @Test
    fun reshape(){
        val arr1 = arrayListOf(1.0,  2.0,  3.0,  4.0,  5.0,  6.0,  7.0,  8.0,  9.0,  10.0,  11.0,  12.0,  13.0,  14.0,  15.0,  16.0);
        val reshape1 = reshapeVector(arr1, 8, 2);
        assertEquals(arrayListOf(
            arrayListOf(1.0,  9.0),
            arrayListOf(2.0,  10.0),
            arrayListOf(3.0,  11.0),
            arrayListOf(4.0,  12.0),
            arrayListOf(5.0,  13.0),
            arrayListOf(6.0,  14.0),
            arrayListOf(7.0,  15.0),
            arrayListOf(8.0,  16.0)
        ), reshape1)

        val arr2 = arrayListOf(1.0,  2.0,  3.0,  4.0,  5.0,  6.0);
        val reshape2 = reshapeVector(arr2, 2, 3);
        assertEquals(arrayListOf(
            arrayListOf(1.0,  3.0,  5.0),
            arrayListOf(2.0,  4.0,  6.0)
        ), reshape2)
    }

    @Test
    fun getMatrixMultiplication(){

        val matrix1: List<List<Double>> = arrayListOf(arrayListOf(1.0,  2.0), arrayListOf(3.0,  4.0));
        val matrix2: List<List<Double>> = arrayListOf(arrayListOf(1.0,  3.0,  6.0), arrayListOf(3.0,  7.0,  8.0));

        assertEquals(
            arrayListOf(arrayListOf(7.0,  17.0,  22.0), arrayListOf(15.0,  37.0,  50.0)),
            getMatrixProduct(matrix1, matrix2)
        )
    }

    @Test
    fun sigmoidFun(){

        assertEquals(0.7310586, sigmoid(1.0), 0.0001)
        assertEquals(0.999089, sigmoid(7.0), 0.0001)

    }

    @Test
    fun convertLinkedListDoubleToRaw() {
        val queueAccelerometerX = LinkedList<Double>()
        queueAccelerometerX.add(1.0)
        queueAccelerometerX.add(2.0)
        queueAccelerometerX.add(3.0)
        val result = convertLinkedListDoubleToRawString(queueAccelerometerX);
        println(result)
        assertEquals("1.0,2.0,3.0", result)
    }

    @Test
    fun convertRawToLinkedListDouble() {
        val converted = convertRawStringToLinkedListDouble("1.0,2.0,3.0");
        println(converted)
        assertEquals(3, converted.size);
    }

    @Test
    fun convertMatrixOfLinkedListDoubleToRaw() {
        val queueAccelerometerX = LinkedList<Double>()
        queueAccelerometerX.add(1.0)
        queueAccelerometerX.add(2.0)
        queueAccelerometerX.add(3.0)
        val queueAccelerometerY = LinkedList<Double>()
        queueAccelerometerY.add(4.0)
        queueAccelerometerY.add(5.0)
        queueAccelerometerY.add(6.0)
        val queueAccelerometerZ = LinkedList<Double>()
        queueAccelerometerZ.add(7.0)
        queueAccelerometerZ.add(8.0)
        queueAccelerometerZ.add(9.0)

        val matrix = ArrayList<LinkedList<Double>>()
        matrix.add(queueAccelerometerX)
        matrix.add(queueAccelerometerY)
        matrix.add(queueAccelerometerZ)

        val result = convertMatrixOfLinkedListDoubleToRawString(matrix);
        println(result)
        assertEquals("1.0,2.0,3.0#4.0,5.0,6.0#7.0,8.0,9.0", result)
    }

    @Test
    fun convertRawToMatrixOfLinkedListDouble() {
        val converted = convertRawStringToMatrixOfLinkedListDouble("1.0,2.0,3.0#4.0,5.0,6.0#7.0,8.0,9.0");
        println(converted)

        assertEquals(3, converted.size);
        assertEquals(3, converted[0].size);
        assertEquals(3, converted[1].size);
        assertEquals(3, converted[2].size);
    }

    @Test
    fun writeMatrix() {
        val positive = LinkedList<Double>()
        for(i in 1..100){
            positive.add(i.toDouble())
        }

        val negative = LinkedList<Double>()
        for(i in 1..100){
            negative.add(i.toDouble() *-1)
        }

        val neutral = LinkedList<Double>()
        for(i in 1..100){
            neutral.add(0.0)
        }

        val matrix_positive = ArrayList<LinkedList<Double>>()
        for(examples in 1..50){
            matrix_positive.add(positive)
        }
        val matrix_negative = ArrayList<LinkedList<Double>>()
        for(examples in 1..50){
            matrix_negative.add(negative)
        }
        val matrix_neutral = ArrayList<LinkedList<Double>>()
        for(examples in 1..50){
            matrix_neutral.add(neutral)
        }

        writeGestureData(context, "data,user_test,function15.txt", matrix_negative, matrix_negative, matrix_negative, matrix_negative, matrix_negative, matrix_negative)
        writeGestureData(context, "data,user_test,function16.txt", matrix_neutral, matrix_neutral, matrix_neutral, matrix_neutral, matrix_neutral, matrix_neutral)
        writeGestureData(context, "data,user_test,function17.txt", matrix_positive, matrix_positive, matrix_positive, matrix_positive, matrix_positive, matrix_positive)

    }

    @Test
    fun readMatrix() {
        //writeMatrix()

        val filename: String = "testno.txt";
        val write_before = true;

        val queueAccelerometerX = LinkedList<Double>()
        queueAccelerometerX.add(1.0)
        queueAccelerometerX.add(2.0)
        queueAccelerometerX.add(3.0)
        val queueAccelerometerY = LinkedList<Double>()
        queueAccelerometerY.add(4.0)
        queueAccelerometerY.add(5.0)
        queueAccelerometerY.add(6.0)
        val queueAccelerometerZ = LinkedList<Double>()
        queueAccelerometerZ.add(7.0)
        queueAccelerometerZ.add(8.0)
        queueAccelerometerZ.add(9.0)
        val queueGyroscopeX = LinkedList<Double>()
        queueGyroscopeX.add(1.0)
        queueGyroscopeX.add(2.0)
        queueGyroscopeX.add(3.0)
        val queueGyroscopeY = LinkedList<Double>()
        queueGyroscopeY.add(4.0)
        queueGyroscopeY.add(5.0)
        queueGyroscopeY.add(6.0)
        val queueGyroscopeZ = LinkedList<Double>()
        queueGyroscopeZ.add(7.0)
        queueGyroscopeZ.add(8.0)
        queueGyroscopeZ.add(9.0)

        if(write_before)
            writeGestureData(context, filename,
                arrayListOf(queueAccelerometerX), arrayListOf(queueAccelerometerY), arrayListOf(queueAccelerometerZ),
                arrayListOf(queueGyroscopeX), arrayListOf(queueGyroscopeY), arrayListOf(queueGyroscopeZ))



        val gestureFullData = readGestureData(context, filename, -999);



        if(gestureFullData.label == -1 || gestureFullData.accelerometerXs.size == 0){
            writeGestureData(context, filename,
                arrayListOf(queueAccelerometerX), arrayListOf(queueAccelerometerY), arrayListOf(queueAccelerometerZ),
                arrayListOf(queueGyroscopeX), arrayListOf(queueGyroscopeY), arrayListOf(queueGyroscopeZ))
        }else{

            val holderAccelerometerX = ArrayList<List<Double>>()
            holderAccelerometerX.addAll(gestureFullData.accelerometerXs)
            holderAccelerometerX.add(queueAccelerometerX)

            val holderAccelerometerY = ArrayList<List<Double>>()
            holderAccelerometerY.addAll(gestureFullData.accelerometerYs)
            holderAccelerometerY.add(queueAccelerometerY)

            val holderAccelerometerZ = ArrayList<List<Double>>()
            holderAccelerometerZ.addAll(gestureFullData.accelerometerZs)
            holderAccelerometerZ.add(queueAccelerometerZ)

            val holderGyroscopeX = ArrayList<List<Double>>()
            holderGyroscopeX.addAll(gestureFullData.gyroscopeXs)
            holderGyroscopeX.add(queueGyroscopeX)

            val holderGyroscopeY = ArrayList<List<Double>>()
            holderGyroscopeY.addAll(gestureFullData.gyroscopeYs)
            holderGyroscopeY.add(queueGyroscopeY)

            val holderGyroscopeZ = ArrayList<List<Double>>()
            holderGyroscopeZ.addAll(gestureFullData.gyroscopeZs)
            holderGyroscopeZ.add(queueGyroscopeZ)

            writeGestureData(context, filename,
                holderAccelerometerX, holderAccelerometerY, holderAccelerometerZ,
                holderGyroscopeX, holderGyroscopeY, holderGyroscopeZ
            )

            val gestureFullData2 = readGestureData(context, filename, -999);
            println("END")

        }



    }

    @Test
    fun fileIds(){

        var id_function = 15;
        val filename = "testIds.txt";
        val write_something_before = true;
        val change_id_function = true;

        if(write_something_before){
            MyFileManager.writeToFile(convertIntListToRawString(arrayListOf(id_function)), context, filename)
            if(change_id_function) id_function = 18;
        }

        val raw_id_functions: String? = MyFileManager.readFromFile(context, filename)
        if(raw_id_functions.equals("") || raw_id_functions == null){
            // archivo no existe
            MyFileManager.writeToFile(convertIntListToRawString(arrayListOf(id_function)), context, filename)
            println(MyFileManager.readFromFile(context, filename))


        }else{
            val arraw_id_functions: ArrayList<Int> = convertRawStringToListInt(raw_id_functions.replace("\n", ""))
            if(!arraw_id_functions.contains(id_function)){
                arraw_id_functions.add(id_function)
            }
            MyFileManager.writeToFile(convertIntListToRawString(arraw_id_functions), context, filename)
            println(MyFileManager.readFromFile(context, filename)
                ?.replace("\n", "")?.let { convertRawStringToListInt(it) })
        }
    }

    @Test
    fun logFun(){

        val values: List<Double> = arrayListOf(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0)
        println(
            values.map { ln(it) }
        )
    }


    @Test
    fun costFun(){

        val nn = NeuralNetwork();
        nn.thetas_rolled = arrayListOf(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0);
        nn.config.neurons_per_layer = intArrayOf(2,3)

        println(nn.costFunction(arrayListOf(arrayListOf(1.0,2.0)), arrayListOf(arrayListOf(0))))
    }

    @Test
    fun gradientFun(){
        val nn = NeuralNetwork();
        nn.thetas_rolled = arrayListOf(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0, 13.0,14.0,15.0);
        nn.config.neurons_per_layer = intArrayOf(2,3,1,1)

        println(nn.calculateGradient(arrayListOf(1.0,2.0), arrayListOf(0)))
    }


    @Test
    fun app_works() {

        val lambda = 0.0;  // si es muy alto provoca underfiting por Hipotesis = 0
        val alpha = 0.1;
        val epochs = 5;

        val neuralNetwork = NeuralNetwork()
        neuralNetwork.config.neurons_per_layer = intArrayOf(-1, 35, 18, -1); // 35 18

        // X (examplesxfeatures
        // y (examplesx1)

        val verbose = true;

        val ids_functions_gestures = arrayListOf<Int>(15, 16, 17);

        val (X, y) = getData(context, 0.1,
            -1,100,false,
            22,30,"user_test",
            true,42, ids_functions_gestures, verbose
        );


        val (X_y_train, X_y_test, X_y_validation) = split_train_test_validation(X, y, 0.7, 0.15);
        val(X_train, y_train) = X_y_train;
        val(X_test, y_test) = X_y_test;
        val(X_validation, y_validation) = X_y_validation;

        if(verbose){
            println("app => X_train: ${X_train.size}, y_train: ${y_train.size}")
            println("app => X_test: ${X_test.size}, y_test: ${y_test.size}")
            println("app => X_val: ${X_validation.size}, y_validation: ${y_validation.size}")
        }

        val number_features = X[0].size;  // número de caracteristicas, en este caso son 600
        val number_clases = ids_functions_gestures.size;  // número de clases

        neuralNetwork.config.neurons_per_layer[0] = number_features;
        neuralNetwork.config.neurons_per_layer[neuralNetwork.config.neurons_per_layer.size-1] = number_clases;

        if(verbose){
            println("Neurons per layer:")
            for(n in 0..(neuralNetwork.config.neurons_per_layer.size-1)){
                print("${neuralNetwork.config.neurons_per_layer[n]} ")
            }
            println()
        };

        // los pesos solo son un vector enrrollado
        neuralNetwork.initWeights();


        val (Js_train: List<Double>, Js_validation: List<Double>) = neuralNetwork.train(
            X_train, y_train, X_validation, y_validation
        );


        val J_test: Double = neuralNetwork.costFunction(X_test, y_test);
        if(verbose){
            println("Js_train: $Js_train")
            println("Js_validation: $Js_validation")
            println("Error final de test: $J_test")
        }

        val (predictions_train, logits_train) = neuralNetwork.predict(X_train);
        val (predictions_test, logits_test) = neuralNetwork.predict(X_test);

        if(verbose){
            var sum_accuracy_train = 0.0;
            for(i in 0..(y_train.size-1)){
                val pred = predictions_train[i][0];
                val real = y_train[i][0];
                if(pred == real){
                    sum_accuracy_train += 1;
                }
            }
            val accuracy_train: Double = (sum_accuracy_train/y_train.size) * 100;

            var sum_accuracy_test: Double = 0.0;
            for(i in 0..(y_test.size-1)){
                if(predictions_test[i][0] == y_test[i][0]){
                    sum_accuracy_test += 1;
                }
            }
            val accuracy_test = (sum_accuracy_test/y_test.size) * 100f;

            println("Accuracy: Train: $accuracy_train%, Test: $accuracy_test%")

            neuralNetwork.mapFuntionToName = HashMap()
            neuralNetwork.mapFuntionToName[0] = "hola"
            neuralNetwork.mapFuntionToName[1] = "mundo"
            neuralNetwork.mapClassToFunction = HashMap()
            neuralNetwork.mapClassToFunction[0] = 15
            neuralNetwork.mapClassToFunction[1] = 16
            neuralNetwork.mapClassToFunction[2] = 17


            val nnString = neuralNetwork.saveToString(accuracy_train, accuracy_test);
            MyFileManager.writeToFile(nnString, context, "neuron.txt")

            val recovered: NeuralNetwork? = NeuralNetwork.recoverNeuralNetwork(context, "neuron.txt")
            println(recovered?.thetas_rolled?.size)

        }


    }

    @Test
    fun fastStringSave(){
        var arr = arrayListOf<Double>(1.0, 2.0, 3.0)
        val cadena:String = arr.toString()
        println(cadena)

        println(cadena.substring(1, cadena.length-1).split(","))
    }

    @Test
    fun saveMap(){
        val map =  HashMap<Int, Any>();
        map.put(1, "uno")
        map.put(2, "dos")

        val raw = convertMapToRawString(map);
        println(raw)
        val reconverted = convertRawStringToMap(raw)
        println(reconverted)

    }


    fun printDoubleMatrix(matrix: List<List<Double>>) {
        println("IMPRIMIENDO MATRIZ ${matrix.size} x ${matrix[0].size}")
        matrix.forEach {
            it.forEach {
                print("${it} ")
            }
            println()
        }
    }


}

