@file:Suppress("ReplaceRangeToWithUntil")

package com.example.projectappmobile.models.neural_network

import android.content.Context
import com.example.projectappmobile.models.MyFileManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.ceil
import kotlin.math.floor

fun convertMapToRawString(
    map: HashMap<Int, Any>
): String {
    var output = "";

    var counter = 0
    map.keys.forEach {
        output += "$it,${map.get(it)}"
        counter+=1
        if(counter < map.keys.size){
            output += "#"
        }
    }

    return output
}

fun convertRawStringToMap(
    raw: String
): HashMap<Int, Any> {

    val map = HashMap<Int, Any>()
    if(raw.equals(""))
        return map

    val raw_split = raw.split("#");

    for (i in 0..(raw_split.size-1)){
        val split_key_value = raw_split[i].split(",")
        map.put(split_key_value[0].toInt(), split_key_value[1])
    }

    return map

}


// todo: punto crítico de almacenado de modelo
fun convertLinkedListDoubleToRawString (listFlot: List<Double>) : String {

    return listFlot.toString();
}

fun convertIntListToRawString (listFlot: List<Int>) : String {

    var output = "";

    for(i in 0..(listFlot.size-1)){
        output += listFlot.get(i);
        if(i < listFlot.size-1){
            output += ",";
        }
    }
    return output;
}

fun convertIntArrayToRawString (listFlot: IntArray) : String {

    var output = "";

    for(i in 0..(listFlot.size-1)){
        output += listFlot.get(i);
        if(i < listFlot.size-1){
            output += ",";
        }
    }
    return output;
}

fun convertRawStringToListInt(rawString: String) : ArrayList<Int> {

    val vector = ArrayList<Int>();

    val split_vector = rawString.split(",");
    // println(split_vector)
    split_vector.forEach {
        vector.add(it.toInt())
    }

    return vector
}

fun convertRawStringToLinkedListDouble(rawString: String) : List<Double> {

    val split_vector = rawString.substring(1, rawString.length-1).split(",");

    return split_vector.map{ it.toDouble() }
}

fun convertMatrixOfLinkedListDoubleToRawString (matrixDouble: List<List<Double>>) : String {

    var output = "";

    for(i in 0..(matrixDouble.size-1)){
        output += convertLinkedListDoubleToRawString(matrixDouble.get(i));
        if(i < matrixDouble.size-1){
            output += "#";
        }
    }
    return output;
}

fun convertRawStringToMatrixOfLinkedListDouble(rawString: String) : List<List<Double>> {

    val matrix = ArrayList<List<Double>>();
    val split_matrix = rawString.split("#");

    split_matrix.forEach {
        matrix.add(convertRawStringToLinkedListDouble(it))
    }

    return matrix;

}


fun writeGestureData(
    context: Context, filename: String,
    accelerometerXs: List<List<Double>>, accelerometerYs: List<List<Double>>,
    accelerometerZs: List<List<Double>>, gyroscopeXs: List<List<Double>>,
    gyroscopeYs: List<List<Double>>, gyroscopeZs: List<List<Double>>
) {
    val output =
        convertMatrixOfLinkedListDoubleToRawString(accelerometerXs) + "\n" +
                convertMatrixOfLinkedListDoubleToRawString(accelerometerYs) + "\n" +
                convertMatrixOfLinkedListDoubleToRawString(accelerometerZs) + "\n" +
                convertMatrixOfLinkedListDoubleToRawString(gyroscopeXs) + "\n" +
                convertMatrixOfLinkedListDoubleToRawString(gyroscopeYs) + "\n" +
                convertMatrixOfLinkedListDoubleToRawString(gyroscopeZs);
    MyFileManager.writeToFile(output, context, filename);
}

fun readGestureData(
    context: Context, filename: String, label: Int
) : GestureFullData {
    val accelerometerXs : List<List<Double>>;
    val accelerometerYs : List<List<Double>>;
    val accelerometerZs : List<List<Double>>;
    val gyroscopeXs : List<List<Double>>;
    val gyroscopeYs : List<List<Double>>;
    val gyroscopeZs : List<List<Double>>;

    val text = MyFileManager.readFromFile(context, filename)

    val split_new_line = text?.split("\n");

    if(split_new_line != null && !text.equals("") && !split_new_line.equals("")) {
        accelerometerXs = convertRawStringToMatrixOfLinkedListDouble(split_new_line.get(0))
        accelerometerYs = convertRawStringToMatrixOfLinkedListDouble(split_new_line.get(1))
        accelerometerZs = convertRawStringToMatrixOfLinkedListDouble(split_new_line.get(2))
        gyroscopeXs = convertRawStringToMatrixOfLinkedListDouble(split_new_line.get(3))
        gyroscopeYs = convertRawStringToMatrixOfLinkedListDouble(split_new_line.get(4))
        gyroscopeZs = convertRawStringToMatrixOfLinkedListDouble(split_new_line.get(5))
        return GestureFullData(label, accelerometerXs, accelerometerYs, accelerometerZs, gyroscopeXs, gyroscopeYs, gyroscopeZs)
    }else{
        println("readData: Los datos leidos están mal parseados o el archivo no existe")
        return GestureFullData(-1, arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())
    }





}


fun getData(context: Context, augmentation_percent_for_shifting: Double = 0.1,
            samples: Int = -1, parts_for_gesture: Int =100,
            normalize: Boolean = false,
            init_forecast: Int =10, limit_forecast: Int =30,
            user: String = "user_test",
            shuffle: Boolean = true,
            seed_shuffle: Long = 42,
            id_functions: List<Int>,
            verbose: Boolean = false
): Triple<List<List<Double>>, List<List<Int>>, String> {


    var output = ""

    var X = ArrayList<List<Double>>();
    var y = ArrayList<List<Int>>();

    var counter_data_augmented_by_shift = 0;
    var counter_data_augmented_by_move_average = 0;
    var index_begin = 0;
    var index_end = 0;

    for (i in 0..(id_functions.size-1)) {
        val id_function = id_functions.get(i);
        val filename = "data,$user,function$id_function.txt";
        val gestureFullData = readGestureData(context, "$filename", i);

        if(normalize){
            gestureFullData.normalize()
        }

        val m = gestureFullData.accelerometerXs.size;

        output += "File: $filename, has: $m samples\n"

        index_end += m;

        X.addAll(fusionMatrixByHorizontal(
            gestureFullData.accelerometerXs, gestureFullData.accelerometerYs, gestureFullData.accelerometerZs,
            gestureFullData.gyroscopeXs, gestureFullData.gyroscopeYs, gestureFullData.gyroscopeZs
        ));

        for (i_y in index_begin..(index_end-1)){
            y.add(List(1) {gestureFullData.label})
        }
        index_begin += m;

        val threshold_augmentation: Int = floor(parts_for_gesture * augmentation_percent_for_shifting).toInt();

        for(index_sample in 0..(m-1))   // por cada ejemplo
        {
            val original_data_accelerometer_x =  gestureFullData.accelerometerXs.get(index_sample); // 1x100
            val original_data_accelerometer_y =  gestureFullData.accelerometerYs.get(index_sample); // 1x100
            val original_data_accelerometer_z =  gestureFullData.accelerometerZs.get(index_sample); // 1x100
            val original_data_gyroscope_x =  gestureFullData.gyroscopeXs.get(index_sample); // 1x100
            val original_data_gyroscope_y =  gestureFullData.gyroscopeYs.get(index_sample); // 1x100
            val original_data_gyroscope_z =  gestureFullData.gyroscopeZs.get(index_sample); // 1x100

            // println("original ax: $original_data_accelerometer_x")

            val (X_shift_augmentation, y_shift_augmentation) = generateAugmentationDataForSensorsByShifting(
                threshold_augmentation, gestureFullData.label, original_data_accelerometer_x,
                original_data_accelerometer_y, original_data_accelerometer_z,
                original_data_gyroscope_x, original_data_gyroscope_y, original_data_gyroscope_z
            )

            if(X_shift_augmentation.size > 0){
                X.addAll(X_shift_augmentation)
                y.addAll(y_shift_augmentation)
            }

            if(init_forecast > 2 && limit_forecast > init_forecast){
                val (X_mabx_augmentation, y_mabx_augmentation) = generateAugmentationDataForSensorsByMoveAverageBox(
                    init_forecast, limit_forecast, gestureFullData.label, original_data_accelerometer_x,
                    original_data_accelerometer_y, original_data_accelerometer_z,
                    original_data_gyroscope_x, original_data_gyroscope_y, original_data_gyroscope_z
                )

                if(X_mabx_augmentation.size > 0){
                    X.addAll(X_mabx_augmentation)
                    y.addAll(y_mabx_augmentation)
                }
            }

        }
        counter_data_augmented_by_shift += 2 * m * threshold_augmentation;
        counter_data_augmented_by_move_average += (limit_forecast-init_forecast+1) * m;

    }

    if(verbose){
        println("getData=> if Valid data threshold_augmentation<=features: threshold_augmentation: counter_data_augmented_by_shift: $counter_data_augmented_by_shift")
        println("getData=> if Valid data init_forecast < limit_forecast <= features: counter_data_augmented_by_move_average: $counter_data_augmented_by_move_average")
        println("getData=> SIZE: ${X.size}");


    }

    output += "counter_data_augmented_by_shift: $counter_data_augmented_by_shift\n"
    output += "counter_data_augmented_by_move_average: $counter_data_augmented_by_move_average\n"
    output += "Full size: ${X.size}, Real data size ${X.size-counter_data_augmented_by_move_average-counter_data_augmented_by_shift}"

    if(shuffle){
        X.shuffle(Random(seed_shuffle))
        y.shuffle(Random(seed_shuffle))
    }


    if(samples >=  1){
        val limit_samples = if(samples <= X.size) samples else X.size;
        X = ArrayList(X.subList(0, limit_samples));
        y = ArrayList(y.subList(0, limit_samples));
    }

    return Triple(X, y, output)
}

fun generateAugmentationDataForSensorsByShifting(
    threshold_augmentation: Int,
    label: Int,
    original_data_accelerometer_x: List<Double>,
    original_data_accelerometer_y: List<Double>,
    original_data_accelerometer_z: List<Double>,
    original_data_gyroscope_x: List<Double>,
    original_data_gyroscope_y: List<Double>,
    original_data_gyroscope_z: List<Double>
): Pair<List<List<Double>>, List<List<Int>>> {

    val X_augmentation = ArrayList<List<Double>>();
    val y_augmentation = ArrayList<List<Int>>();

    val limit_shifting = if(threshold_augmentation <= original_data_accelerometer_x.size) threshold_augmentation else original_data_accelerometer_x.size;


    for(shift in 1..limit_shifting){
        val augmented_data_accelerometer_x_left = shiftVector(original_data_accelerometer_x, shift, 0);
        val augmented_data_accelerometer_x_right = shiftVector(original_data_accelerometer_x, shift, 1);

        val augmented_data_accelerometer_y_left = shiftVector(original_data_accelerometer_y, shift, 0);
        val augmented_data_accelerometer_y_right = shiftVector(original_data_accelerometer_y, shift, 1);

        val augmented_data_accelerometer_z_left = shiftVector(original_data_accelerometer_z, shift, 0);
        val augmented_data_accelerometer_z_right = shiftVector(original_data_accelerometer_z, shift, 1);

        val augmented_data_gyroscope_x_left = shiftVector(original_data_gyroscope_x, shift, 0);
        val augmented_data_gyroscope_x_right = shiftVector(original_data_gyroscope_x, shift, 1);

        val augmented_data_gyroscope_y_left = shiftVector(original_data_gyroscope_y, shift, 0);
        val augmented_data_gyroscope_y_right = shiftVector(original_data_gyroscope_y, shift, 1);

        val augmented_data_gyroscope_z_left = shiftVector(original_data_gyroscope_z, shift, 0);
        val augmented_data_gyroscope_z_right = shiftVector(original_data_gyroscope_z, shift, 1);

        X_augmentation.add(fusionVectorByHorizontal(augmented_data_accelerometer_x_left, augmented_data_accelerometer_y_left, augmented_data_accelerometer_z_left, augmented_data_gyroscope_x_left, augmented_data_gyroscope_y_left, augmented_data_gyroscope_z_left));
        X_augmentation.add(fusionVectorByHorizontal(augmented_data_accelerometer_x_right, augmented_data_accelerometer_y_right, augmented_data_accelerometer_z_right, augmented_data_gyroscope_x_right, augmented_data_gyroscope_y_right, augmented_data_gyroscope_z_right));

        for (i_y in 1..2){
            y_augmentation.add(List(1) {label})
        }

    }

    return Pair(X_augmentation, y_augmentation)

}

fun generateAugmentationDataForSensorsByMoveAverageBox(
    init_forecast: Int,
    limit_forecast: Int,
    label: Int,
    original_data_accelerometer_x: List<Double>,
    original_data_accelerometer_y: List<Double>,
    original_data_accelerometer_z: List<Double>,
    original_data_gyroscope_x: List<Double>,
    original_data_gyroscope_y: List<Double>,
    original_data_gyroscope_z: List<Double>
): Pair<List<List<Double>>, List<List<Int>>>{

    val X_augmentation = ArrayList<List<Double>>();
    val y_augmentation = ArrayList<List<Int>>();


    val limit_forecasting = if(limit_forecast <= original_data_accelerometer_x.size) limit_forecast else original_data_accelerometer_x.size;
    val init_forecasting = if(init_forecast <= limit_forecasting) init_forecast else 2;


    for(forecast in init_forecasting..limit_forecasting){

        X_augmentation.add(fusionVectorByHorizontal(
            moveAverageBoxVector(original_data_accelerometer_x, forecast),
            moveAverageBoxVector(original_data_accelerometer_y, forecast),
            moveAverageBoxVector(original_data_accelerometer_z, forecast),
            moveAverageBoxVector(original_data_gyroscope_x, forecast),
            moveAverageBoxVector(original_data_gyroscope_y, forecast),
            moveAverageBoxVector(original_data_gyroscope_z, forecast)
        ));
        y_augmentation.add(List(1) {label});


    }

    return Pair(X_augmentation, y_augmentation)

}

fun moveAverageBoxVector(
    vector: List<Double>,
    forecast_moving: Int
): ArrayList<Double> {

    val len = vector.size;
    val new_vector = ArrayList<Double>();



    val step: Int = ceil(forecast_moving/2f).toInt();

    // len 10
    // forecast_moving = 3
    // step = 2
    // i va de 1..8 de un arreglo [0,1,2,...8,9]

    for(i in 0..(step-2)){
        new_vector.add(0.0)
    }
    for(i in (step-1)..(len-step)){
        val part = vector.subList(i-(step-1), i+(step-1)+1);
        new_vector.add((part.sum())/forecast_moving)
       //println("control")
    }
    for(i in (len-step+1)..(len-1)){
        new_vector.add(0.0)
    }

    return new_vector;
}

fun shiftVector(
    vector: List<Double>,
    positions: Int,
    direction:Int = 0
): ArrayList<Double> {
    val vector_length = vector.size;
    val new_vector = ArrayList<Double>();

    if(direction == 0)  // izquierda
    {
        for(i in 1..positions){
            new_vector.add(0.0)
        }
        for(i in 0..(vector_length-positions-1)){
            new_vector.add(vector[i])
        }
    }
    else if(direction == 1)
    {

        for(i in positions..(vector_length-1)){
            new_vector.add(vector[i])
        }
        for(i in 1..positions){
            new_vector.add(0.0)
        }
    }

    return new_vector


}


fun split_train_test_validation(
    X_original: List<List<Double>>,
    y_original: List<List<Int>>,
    train_portion: Double = 0.8,
    validation_portion: Double = 0.1,
    shuffle: Boolean = false,
    shuffle_seed: Long = 42
): Triple<Pair<List<List<Double>>, List<List<Int>>>, Pair<List<List<Double>>, List<List<Int>>>, Pair<List<List<Double>>, List<List<Int>>>> {
    val test_portion = 1 - train_portion - validation_portion;

    val m = X_original.size  // número de ejemplos

    val X = ArrayList<List<Double>>(X_original);
    val y = ArrayList<List<Int>>(y_original);

    if(shuffle){
        X.shuffle(Random(shuffle_seed))
        y.shuffle(Random(shuffle_seed))
    }

    val m_train = ceil(m * train_portion).toInt();
    // sublist va de [0, limite(, por lo tanto no se toma el último valor
    val X_train = ArrayList(X.subList(0, m_train));
    val y_train = ArrayList(y.subList(0, m_train));

    val m_test = ceil(m * test_portion).toInt();
    val X_test = ArrayList(X.subList(m_train, m_test+m_train));
    val y_test = ArrayList(y.subList(m_train, m_test+m_train));

    val X_validation = ArrayList(X.subList(m_test+m_train, X.size));
    val y_validation = ArrayList(y.subList(m_test+m_train, y.size));


    return Triple(Pair(X_train, y_train), Pair(X_test, y_test), Pair(X_validation, y_validation))

}


fun fusionMatrixByHorizontal(vararg data_matrix: List<List<Double>>): List<List<Double>>{

    val fullMatrix = ArrayList<ArrayList<Double>>();
    val m = data_matrix[0].size;
    // val features = data_matrix[0][0].size;

    for(i in 0..(m-1)){
        val arr = ArrayList<Double>();

        for(index_matrix in 0..(data_matrix.size-1)){
            arr.addAll(data_matrix[index_matrix][i])
        }
        fullMatrix.add(arr)

    }

    return fullMatrix
}

fun fusionVectorByHorizontal(vararg data_vector: List<Double>): List<Double>{

    val fullVector = ArrayList<Double>();

    for(index_vector in 0..(data_vector.size-1)){
        fullVector.addAll(data_vector[index_vector])
    }

    return fullVector
}



fun sparse_one_hot_encoding(
    y: List<List<Int>>,
    number_classes: Int
): List<List<Double>>{

    val y_sparse =  ArrayList<List<Double>>();

    for(i in 0..(y.size-1)){
        val arr = ArrayList<Double>();
        for(sparse in (0..number_classes-1)){
            arr.add(if(sparse.toDouble() == y[i][0].toDouble()) 1.0 else 0.0)
        }
        y_sparse.add(arr)
    }

    return y_sparse


}

fun sparse_one_hot_encoding_vector(
    y: List<Int>,
    number_classes: Int
): List<Double>{

    val y_sparse =  ArrayList<Double>();

    for(sparse in (0..(number_classes-1))){
        y_sparse.add(if(sparse.toDouble() == y[0].toDouble()) 1.0 else 0.0)
    }

    return y_sparse


}