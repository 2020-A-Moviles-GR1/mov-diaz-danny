package com.example.projectappmobile.models.neural_network

import java.lang.Exception
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

fun getSumMatrix(matrix: List<List<Double>>): Double{
    var sum = 0.0;

    for(i in 0..(matrix.size-1)){
        for(j in 0..(matrix[0].size-1)){
            sum += matrix[i][j];
        }
    }
    return sum
}

fun getStdMatrix(matrix: List<List<Double>>, mean: Double): Double{
    var sum = 0.0;

    for(i in 0..(matrix.size-1)){
        for(j in 0..(matrix[0].size-1)){
            sum += (matrix[i][j] - mean).pow(2);
        }
    }

    return sqrt(sum/(matrix.size*matrix[0].size - 1))

}

fun getMatrixProduct(
    matrix1: List<List<Double>>,
    matrix2: List<List<Double>>
): List<List<Double>>{


    if(matrix1[0].size != matrix2.size){
        throw Exception("Invalid multiplication")
    }
    val result = ArrayList<List<Double>>()



    for(i in 0..(matrix1.size-1)){
        val arr = ArrayList<Double>();
        for(k in 0..(matrix2[0].size-1)){
            var sum = 0.0;
            for(j in 0..(matrix1[0].size-1)){
                sum += matrix1[i][j] * matrix2[j][k]
            }
            arr.add(sum)
        }
        result.addAll(listOf(arr))
    }

    return result

}

fun getVectorMatrixProduct(
    vector: List<Double>,
    matrix: List<List<Double>>
): List<Double> {


    if(vector.size != matrix.size){
        throw Exception("Invalid multiplication vector-matrix")
    }

    val result = ArrayList<Double>()

    for(k in 0..(matrix[0].size-1)){
        var sum = 0.0;
        for(j in 0..(vector.size-1)){
            sum += vector[j] * matrix[j][k]
        }
        result.add(sum)
    }

    return result
}

fun getMatrixVectorProduct(
    matrix: List<List<Double>>,
    vector: List<Double>
): List<List<Double>> {


    if(vector.size != matrix[0].size){
        throw Exception("Invalid multiplication matrix-vector")
    }

    val result = ArrayList<List<Double>>()

    for(i in 0..(matrix.size-1)){

        val arr = ArrayList<Double>()
        for(j in 0..(matrix[0].size-1)){
            arr.add(matrix[i][j] * vector[j])
        }
        result.addAll(listOf(arr))
    }

    return result
}


fun getDirectMatrixProduct(
    matrix1: List<List<Double>>,
    matrix2: List<List<Double>>
): List<List<Double>>{

    val result = ArrayList<List<Double>>()

    if(matrix1.size != matrix2.size || matrix1[0].size != matrix2[0].size){
        throw Exception("Invalid multiplication for direct")
    }


    for(i in 0..(matrix1.size-1)){
        val arr = ArrayList<Double>();
        for(j in 0..(matrix1[0].size-1)){
            arr.add(matrix1[i][j] * matrix2[i][j])
        }
        result.addAll(listOf(arr))
    }

    return result

}

fun getMatrix1MinusMatrix2(
    matrix1: List<List<Double>>,
    matrix2: List<List<Double>>
): List<List<Double>>{

    val result = ArrayList<List<Double>>()

    if(matrix1.size != matrix2.size || matrix1[0].size != matrix2[0].size){
        throw Exception("Invalid multiplication for direct matrix1-matrix2")
    }


    for(i in 0..(matrix1.size-1)){
        val arr = ArrayList<Double>();
        for(j in 0..(matrix1[0].size-1)){
            arr.add(matrix1[i][j] - matrix2[i][j])
        }
        result.addAll(listOf(arr))
    }

    return result

}


fun getMatrix1PlusMatrix2(
    matrix1: List<List<Double>>,
    matrix2: List<List<Double>>
): List<List<Double>>{

    val result = ArrayList<List<Double>>()

    if(matrix1.size != matrix2.size || matrix1[0].size != matrix2[0].size){
        throw Exception("Invalid sum for direct matrix1+matrix2")
    }


    for(i in 0..(matrix1.size-1)){
        val arr = ArrayList<Double>();
        for(j in 0..(matrix1[0].size-1)){
            arr.add(matrix1[i][j] + matrix2[i][j])
        }
        result.addAll(listOf(arr))
    }

    return result

}

fun getVector1MinusVector2(
    vector1: List<Double>,
    vector2: List<Double>
): List<Double>{

    val result = ArrayList<Double>()

    if(vector1.size != vector2.size ){
        throw Exception("Invalid multiplication for direct vector1-vector2")
    }


    for(i in 0..(vector1.size-1)){
        result.add(vector1[i] - vector2[i])
    }

    return result

}


fun normalize(matrix: List<List<Double>>, mean: Double, std: Double) : List<List<Double>>{

    if(std == 0.0){
        return matrix;
    }
    return matrix.map{

            arr ->

        val new_arr = arr.map{ (it - mean)/std }

        return@map new_arr

    }
}

fun transposeVector(
    vector: List<Double>
): List<List<Double>>{

    val result = ArrayList<List<Double>>();

    for(i in 0..(vector.size-1)){
        result.add(arrayListOf(vector[i]))
    }

    return result

}

fun transposeMatrix(
    matrix: List<List<Double>>
): List<List<Double>>{

    val result = ArrayList<List<Double>>();

    for(j in 0..(matrix[0].size-1)){
        val arr = ArrayList<Double>()
        for(i in 0..(matrix.size-1)){
            arr.add(matrix[i][j])
        }
        result.add(arr)
    }

    return result

}


fun getMatrixDecompose(
    matrix: List<List<Double>>
): List<Double> {

    val result = ArrayList<Double>()

    for(j in 0..(matrix[0].size-1)){
        for(i in 0..(matrix.size-1)){
            result.add(matrix[i][j])
        }
    }

    return result

}


fun reshapeVector(
    vector: ArrayList<Double>,
    rows: Int,
    cols: Int
): List<List<Double>> {
    /*
    reshape test
    a =  1    2    3    4    5    6    7    8    9   10   11   12   13   14   15   16
    reshape(a, 8, 2) =
    1    9
    2   10
    ..  ..
    7   15
    8   16
    fevuelve rowsxcols
    */
    val len: Int = vector.size;

    if(len != (rows*cols)){
        throw Exception("Error de dimensión")
    }

    val matrix = ArrayList<ArrayList<Double>>()

    for(i in 0..(rows-1)){
        var index_element = i;
        val row = ArrayList<Double>();
        for(element in 0..(cols-1)){
            row.add(vector[index_element])
            index_element += rows
        }
        matrix.addAll(listOf(row))
    }

    return matrix

}

fun sigmoid(value: Double) : Double {
    return 1.0f / (1+exp(-value));
}

fun sigmoidGradient(value: Double) : Double {
    // derivada de sigmoid: g = sigmoid(z) .* (1 - sigmoid(z));
    return sigmoid(value) * (1- sigmoid(value));
}


fun clampAbsolute(
    value: Double,
    max: Double
): Double {
    if(abs(value) > abs(max)){
        return max
    }else{
        return value
    }
}