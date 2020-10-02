package com.example.projectappmobile.models.neural_network

class GestureFullData (
    val label: Int,
    var accelerometerXs : List<List<Double>>,
    var accelerometerYs : List<List<Double>>,
    var accelerometerZs : List<List<Double>>,
    var gyroscopeXs : List<List<Double>>,
    var gyroscopeYs : List<List<Double>>,
    var gyroscopeZs : List<List<Double>>
){
    fun normalize() {

        val mean_accelerometerXs = getSumMatrix(this.accelerometerXs)/(this.accelerometerXs.size*this.accelerometerXs[0].size);
        this.accelerometerXs = normalize(this.accelerometerXs, mean_accelerometerXs, getStdMatrix(accelerometerXs, mean_accelerometerXs));

        val mean_accelerometerYs = getSumMatrix(this.accelerometerYs)/(this.accelerometerYs.size*this.accelerometerYs[0].size);
        this.accelerometerYs = normalize(this.accelerometerYs, mean_accelerometerYs, getStdMatrix(accelerometerYs, mean_accelerometerYs));

        val mean_accelerometerZs = getSumMatrix(this.accelerometerZs)/(this.accelerometerZs.size*this.accelerometerZs[0].size);
        this.accelerometerZs = normalize(this.accelerometerZs, mean_accelerometerZs, getStdMatrix(accelerometerZs, mean_accelerometerZs));

        val mean_gyroscopeXs = getSumMatrix(this.gyroscopeXs)/(this.gyroscopeXs.size*this.gyroscopeXs[0].size);
        this.gyroscopeXs = normalize(this.gyroscopeXs, mean_gyroscopeXs, getStdMatrix(gyroscopeXs, mean_gyroscopeXs));

        val mean_gyroscopeYs = getSumMatrix(this.gyroscopeYs)/(this.gyroscopeYs.size*this.gyroscopeYs[0].size);
        this.gyroscopeYs = normalize(this.gyroscopeYs, mean_gyroscopeYs, getStdMatrix(gyroscopeYs, mean_gyroscopeYs));

        val mean_gyroscopeZs = getSumMatrix(this.gyroscopeZs)/(this.gyroscopeZs.size*this.gyroscopeZs[0].size);
        this.gyroscopeZs = normalize(this.gyroscopeZs, mean_gyroscopeZs, getStdMatrix(gyroscopeZs, mean_gyroscopeZs));

    }

}