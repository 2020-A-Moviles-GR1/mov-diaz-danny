@file:Suppress("LocalVariableName")

package com.example.projectappmobile.models.neural_network

import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.TextView
import com.example.projectappmobile.models.MyFileManager
import com.example.projectappmobile.models.Parameters
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ln

class NeuralNetwork(
    var config: Config = Config(),
    var is_trained: Boolean = false,
    val presenter: NeuralNetworkInterface? = null
) {

    interface NeuralNetworkInterface {
        fun onEpochBegin(epoch: Int, msg: String)
        fun onEpochPartFinished(actual_m: Int, msg: String)
        fun onUpdateCompleteLog(log: String)
    }

    class Config(
        val lambda: Double = 0.0,
        val alpha: Double = 0.1,
        val epochs: Int = 4,
        val samples: Int = -1,
        val normalize: Boolean = true,
        val shuffle: Boolean = true,
        val verbose: Boolean = true,
        val augmentation_by_shifting_percent_of_data: Double = 0.05,
        val augmentation_by_move_average_box_begin: Int = 12,
        val augmentation_by_move_average_box_end: Int = 20,
        val train_split_portion: Double = 0.8,
        val validation_split_portion: Double = 0.1,
        val random_seed_shuffle: Long = 24,
        val random_seed_init_weights: Long = 42,
        val std_objective_init_weights: Double = 1.0,
        val mean_objective_init_weights: Double = 0.0,
        var neurons_per_layer: IntArray = intArrayOf(-1, 35, 18, -1)
    )


    var thetas_rolled = ArrayList<Double>()

    var mapClassToFunction = HashMap<Int, Any>()
    var mapFuntionToName = HashMap<Int, Any>();

    var accuracy_train = 0.0
    var accuracy_test = 0.0

    private var log: String = ""

    fun initMapClassToFunction(ids_functions_gestures: List<Int>){
        for(index_fun in 0..(ids_functions_gestures.size-1)){
            mapClassToFunction[index_fun] = ids_functions_gestures[index_fun]
        }
    }




    fun initWeights(){

        // neurons_per_layer 1x4
        var sum_total_weights = 0;

        for(i in 0..(this.config.neurons_per_layer.size-2)){
            sum_total_weights += (this.config.neurons_per_layer[i]+1) * this.config.neurons_per_layer[i+1];
        }

        val random = Random(this.config.random_seed_init_weights)
        for(i in 1..sum_total_weights){
            thetas_rolled.add((random.nextGaussian() * this.config.std_objective_init_weights) + this.config.mean_objective_init_weights);
        }

        // normal distribution, mean=0 standard deviation=1

    }

    fun train(
        X_train: List<List<Double>>,
        y_train: List<List<Int>>,
        X_validation: List<List<Double>>,
        y_validation: List<List<Int>>
    ): Pair<ArrayList<Double>, ArrayList<Double>> {

        val Js_train = ArrayList<Double>();
        val Js_validation = ArrayList<Double>();

        val m = X_train.size;

        for(epoch in 1..this.config.epochs){

            val J_train = this.costFunction(X_train, y_train);
            val J_validation = this.costFunction(X_validation, y_validation);

            val resume_epoch_begin = "Epoch: $epoch, J_train: $J_train, J_validation: $J_validation";

            this.addLog(resume_epoch_begin)
            if(this.config.verbose) {
                println(resume_epoch_begin)
            }
            if(presenter != null){
                presenter.onEpochBegin(epoch, resume_epoch_begin)
            }

            //update views
            if(presenter != null){
                presenter.onUpdateCompleteLog(this.getLog())
            }



            Js_train.add(J_train);
            Js_validation.add(J_validation);

            for(i in 0..(m-1)){
                if(this.config.verbose) {
                    if(i%(m/5) == 0 || i == (m-1)){
                        this.addLog("Epoch: $epoch, m: ${i+1} de $m")
                        println("Epoch: $epoch, m: ${i+1} de $m")
                        if(presenter != null){
                            presenter.onUpdateCompleteLog(this.getLog())
                        }
                    }
                }
                // el gradiente también viene enrrollado
                val gradient: List<Double> = this.calculateGradient(X_train[i], y_train[i]);

               //  val alphaXgradient: List<Double> = gradient.map { it * this.config.alpha }

                this.thetas_rolled = ArrayList(this.thetas_rolled.mapIndexed { index, it -> it - (this.config.alpha * gradient[index]) })

                /*for(g in 0..(gradient.size-1)){
                    this.thetas_rolled[g] = this.thetas_rolled[g] -
                }*/


                //println("Control")

            }



        }
        return Pair(Js_train, Js_validation)
    }

    fun costFunction(
        X: List<List<Double>>,
        y: List<List<Int>>
    ): Double {
        val m = X.size;  // número de ejemplos
        val number_layers: Int = this.config.neurons_per_layer.size;  // número de capas

        // Feed Forward
        var index_reshape_begin = 0;
        var index_reshape_end = 0;

        // a = mx(features+1) el +1 es el bias -> neurons_per_layer(i)+1 = features+1 en esta asignación
        var a = addBiasToMatrix(X);


        var regularization_sum = 0.0;

        for(i in 0..(number_layers - 2)){

            index_reshape_end += (this.config.neurons_per_layer[i]+1)*this.config.neurons_per_layer[i+1];
            // theta = (neurons_per_layer(i)+1)x(neurons_per_layer(i+1))
            val theta: List<List<Double>> = reshapeVector(
                ArrayList(thetas_rolled.subList(index_reshape_begin, index_reshape_end)),
                this.config.neurons_per_layer[i]+1, this.config.neurons_per_layer[i+1]
            );



            regularization_sum += getSumMatrix(theta.map {
                    arr ->
                arr.map {
                    it * it
                }
            })


            // z = mx(neurons_per_layer(i)+1) * (neurons_per_layer(i)+1)x(neurons_per_layer(i+1))
            // = mxneurons_per_layer(i+1)
            val z = getMatrixProduct(a, theta);
            // a = mx(neurons_per_layer(i+1)+1)
            a = addBiasToMatrix(
                z.map {
                    arr -> arr.map {
                        sigmoid(it)
                    }
                }
            );
            index_reshape_begin = index_reshape_end;
        }

        // hipotesis es a, la respuesta correcta es y (hot encoded)
        val sparse_y = sparse_one_hot_encoding(y, this.config.neurons_per_layer[number_layers-1]);
        // [1 0 0] si y es 1 y hay 3 clases

        val a_with_out_bias = removeBiasFromMatrix(a);

        val regularization = (this.config.lambda/(2*m)) * regularization_sum;
        val sparse_y_negative = sparse_y.map { arr -> arr.map { it*-1 }};
        val log_a = a_with_out_bias.map { arr -> arr.map { ln(it) } }
        val sparse_y_negativeXlog_a = getDirectMatrixProduct(sparse_y_negative, log_a);
        val one_minus_sparse_y = sparse_y.map{ arr -> arr.map {1-it }};
        val log_one_minus_a = a_with_out_bias.map { arr -> arr.map { ln(1-it)} }
        val one_minus_sparse_yXlog_one_minus_a = getDirectMatrixProduct(one_minus_sparse_y, log_one_minus_a);

        val J = getSumMatrix(getMatrix1MinusMatrix2(sparse_y_negativeXlog_a, one_minus_sparse_yXlog_one_minus_a))/m + regularization;

        return J
    }

    fun calculateGradient(
        X: List<Double>,
        y: List<Int>
    ): List<Double> {
        // el mismo tamaño que thetas_rolled
        var gradient = ArrayList<Double>();


        // X es solo un ejemplo
        val number_layers = this.config.neurons_per_layer.size;

        // Feed Forward



        var index_reshape_begin = 0;
        var index_reshape_end = 0;
        // dimens -> number_layers-1 X 2
        val indexes_reshapes_theta = ArrayList<List<Int>>();


        // 1Xfeatures
        var a = addBiasToVector(X);

        // sum(neurons_per_layer)+number_layers-1
        val activations_functions = ArrayList<Double>();
        // sum(neurons_per_layer)+number_layers-1
        val activations_derivates_functions = ArrayList<Double>();

        // number_layersX2    // 2 por ser inicio y fin
        val indexes_activations_functions = ArrayList<List<Int>>();
        var index_activations_begin = 0;
        var index_activations_end = this.config.neurons_per_layer[0]+1;  //+1 por el bias

        activations_functions.addAll(a)

        indexes_activations_functions.add(arrayListOf(index_activations_begin, index_activations_end));

        for(i in index_activations_begin..(index_activations_end-1)){
            activations_derivates_functions.add(0.0)

        }

        // el derivates deberia llenar con 0 las feature positions?
        index_activations_begin = index_activations_end;

        // todo calcular el error J y los indices al mismo tiempo para optimizar

        for(i in 0..(number_layers-2)){
            index_reshape_end += (this.config.neurons_per_layer[i]+1)*this.config.neurons_per_layer[i+1];
            val theta: List<List<Double>> = reshapeVector(
                ArrayList(thetas_rolled.subList(index_reshape_begin, index_reshape_end)),
                this.config.neurons_per_layer[i]+1, this.config.neurons_per_layer[i+1]
            );

            val z: List<Double> = getVectorMatrixProduct(a, theta);
            // a = 1x(neurons_per_layer(i+1)+1)
            a = addBiasToVector(
                z.map { return@map sigmoid(it) }
            );

            val a_derivate: List<Double> = addBiasToVector(
                z.map { return@map sigmoidGradient(it) }
            );

            index_activations_end += this.config.neurons_per_layer[i+1]+1;

            activations_functions.addAll(a);
            activations_derivates_functions.addAll(a_derivate);

            indexes_reshapes_theta.add(arrayListOf(index_reshape_begin, index_reshape_end));
            indexes_activations_functions.add(arrayListOf(index_activations_begin, index_activations_end));

            index_reshape_begin = index_reshape_end;
            index_activations_begin = index_activations_end;
        }

        // hipotesis es a, la respuesta correcta es y (hot encoded)
        val sparse_y = sparse_one_hot_encoding_vector(y, this.config.neurons_per_layer[number_layers-1]);
        // [1 0 0] si y es 1 y hay 3 clases

        // sum(m x num_labels) => 1 x num_labels; sum(1 x num_labels) => 1x1
                            // val regularization = 0; // (this.config.lambda/(2*m)) * (sum(sum( Theta1(:, 2:end) .^ 2 )) + sum(sum( Theta2(:, 2:end) .^ 2 )));



        // Backpropagation

        var ind: List<Int> = indexes_activations_functions[number_layers-1];
        // la ultima capa de activación no requiere el uno agregado del bias

        // activations_functions[ind[0]:ind[1]](2:end)'
        val h: List<Double> = removeBiasFromVector(ArrayList(activations_functions.subList(ind[0], ind[1])));  // ignora el bias

        val holderDeltaMatrix = ArrayList<List<Double>>()
        holderDeltaMatrix.addAll(arrayListOf(getVector1MinusVector2(h, sparse_y)))

        var delta: List<List<Double>> = holderDeltaMatrix; // 1xlen

        var i =number_layers-2;

        while(i >= 0){
            ind = indexes_activations_functions[i];
            val ind_theta: List<Int> = indexes_reshapes_theta[i];

            // 1 X --
            a = ArrayList(activations_functions.subList(ind[0], ind[1]));  // se requiere el bias
            // derivate no necesita bias
            val a_derivate = removeBiasFromVector(ArrayList(activations_derivates_functions.subList(ind[0], ind[1])));


            // this.neurons_per_layer[i] X neurons_per_layer[i+1]
            val theta: List<List<Double>> = transposeMatrix(
                    removeHorizontalBiasFromMatrix(
                        reshapeVector(
                            ArrayList(thetas_rolled.subList(ind_theta[0], ind_theta[1])),
                            this.config.neurons_per_layer[i]+1, this.config.neurons_per_layer[i+1]
                        )
                    )
                );


            // this.neurons_per_layer[i] X neurons_per_layer[i+1] + 1
            val holderAMatrix = ArrayList<List<Double>>();
            holderAMatrix.add(a)
            val deltaTxa = getMatrixProduct(transposeMatrix(delta), holderAMatrix);

            val grad: List<List<Double>> = transposeMatrix(deltaTxa);
            /*
            regularization
            grad(2:end, :) = grad(2:end, :) + (lambda * theta') / 545;
             */
            val decomposed_grad: List<Double> = getMatrixDecompose(grad);
            val holder_gradient = ArrayList<Double>();
            holder_gradient.addAll(decomposed_grad)
            holder_gradient.addAll(gradient)
            gradient = holder_gradient;
            // se agrega de atras para adelante




            if(i > 0){
                val deltaXtheta: List<List<Double>> = getMatrixProduct(delta, theta);
                val holderADerivateMatrix = ArrayList<List<Double>>();
                holderADerivateMatrix.add(a_derivate)
                delta = getDirectMatrixProduct(deltaXtheta, holderADerivateMatrix) ;
            }

            i -= 1;
        }

        return gradient

    }

    fun predict(
        X: List<List<Double>>
    ): Pair<List<List<Int>>, List<List<Double>>> {
        val number_layers = this.config.neurons_per_layer.size;

        // Feed Forward
        var index_reshape_begin = 0;
        var index_reshape_end = 0;


        var a = addBiasToMatrix(X);

        for(i in 0..(number_layers-2)){
            index_reshape_end += (this.config.neurons_per_layer[i]+1)*this.config.neurons_per_layer[i+1];

            val theta: List<List<Double>> = reshapeVector(
                ArrayList(thetas_rolled.subList(index_reshape_begin, index_reshape_end)),
                this.config.neurons_per_layer[i]+1, this.config.neurons_per_layer[i+1]
            );

            val z = getMatrixProduct(a, theta);
            // a = mx(neurons_per_layer(i+1)+1)
            a = addBiasToMatrix(
                z.map {
                        arr -> arr.map {sigmoid(it)}
                }
            );
            index_reshape_begin = index_reshape_end;
        }

        val logits: List<List<Double>> = removeBiasFromMatrix(a);

        val y = ArrayList<List<Int>>()
        for(i in 0..(logits.size-1)){
            var index = 0;
            var max = logits[i][index];
            for(j in 0..(logits[0].size-1)){ //ignora el bias
                val logit = logits[i][j];
                if(logit > max){
                    index = j;
                    max = logits[i][j];
                }
            }
            y.add(arrayListOf(index))

        }


        return Pair(y, logits)
    }

    private fun addBiasToMatrix(matrix: List<List<Double>>): List<List<Double>>{
        val new_matrix = ArrayList<List<Double>>()
        for (i in 0..(matrix.size-1)){
            val example_with_bias = ArrayList<Double>()
            example_with_bias.add(1.0)
            example_with_bias.addAll(matrix[i])
            new_matrix.addAll(listOf(example_with_bias))
        }
        return new_matrix
    }

    private fun addBiasToVector(vector: List<Double>): List<Double>{
        val new_vector = ArrayList<Double>()
        new_vector.add(1.0)
        new_vector.addAll(vector)
        return new_vector
    }

    private fun removeBiasFromMatrix(matrix: List<List<Double>>): List<List<Double>>{
        val new_matrix = ArrayList<List<Double>>()
        for (i in 0..(matrix.size-1)){
            val example_with_out_bias = ArrayList<Double>()
            for(j in 1..(matrix[0].size-1)){
                example_with_out_bias.add(matrix[i][j])
            }

            new_matrix.addAll(listOf(example_with_out_bias))
        }
        return new_matrix
    }

    private fun removeHorizontalBiasFromMatrix(matrix: List<List<Double>>): List<List<Double>>{
        val new_matrix = ArrayList<List<Double>>()
        for (i in 1..(matrix.size-1)){
            val example_with_out_bias = ArrayList<Double>()
            for(j in 0..(matrix[0].size-1)){
                example_with_out_bias.add(matrix[i][j])
            }

            new_matrix.addAll(listOf(example_with_out_bias))
        }
        return new_matrix
    }

    private fun removeBiasFromVector(vector: List<Double>): List<Double>{
        val new_vector = ArrayList<Double>()
        for (i in 1..(vector.size-1)){
            new_vector.add(vector[i])
        }
        return new_vector
    }

    fun saveToString(
        accuracy_train: Double,
        accuracy_test: Double
    ): String {

        println("Inicio save string")
        var output = ""
        val neurons_arquitecture = ArrayList<Int>()
        for(i in 0..(this.config.neurons_per_layer.size-1)){
            neurons_arquitecture.add(this.config.neurons_per_layer[i])
        }


        output += convertIntListToRawString(neurons_arquitecture) + "\n"
        output += convertLinkedListDoubleToRawString(this.thetas_rolled) + "\n"
        output += "$accuracy_train\n$accuracy_test\n"
        output += convertMapToRawString(mapClassToFunction) + "\n"
        output += convertMapToRawString(mapFuntionToName)

        println("Fin de save string")

        return output
    }

    fun addLog(s: String) {
        this.log += s + "\n"
    }

    fun getLog(): String {
        return this.log
    }

    companion object {
        fun recoverNeuralNetwork(
            context: Context,
            filename: String = Parameters.FILENAME_MY_NN
        ): NeuralNetwork?{

            val raw_nn = MyFileManager.readFromFile(context, filename)

            if(raw_nn.equals("") || raw_nn == null){
                return null
            }else{
                val split_raw_nn = raw_nn.split("\n");
                val neuralNetwork = NeuralNetwork()
                val arrayListNeuronsLayers = convertRawStringToListInt(split_raw_nn[0])

                neuralNetwork.config.neurons_per_layer = IntArray(arrayListNeuronsLayers.size)
                for(i in 0..(arrayListNeuronsLayers.size-1)){
                    neuralNetwork.config.neurons_per_layer[i] = arrayListNeuronsLayers[i]
                }
                neuralNetwork.thetas_rolled = ArrayList(convertRawStringToLinkedListDouble(split_raw_nn[1]))

                neuralNetwork.accuracy_train = split_raw_nn[2].toDouble()
                neuralNetwork.accuracy_test = split_raw_nn[3].toDouble()
                Log.i("NeuralNetwork", "RecoveredAccuracy train: ${split_raw_nn[2]}")
                Log.i("NeuralNetwork", "Recovered Accuracy test: ${split_raw_nn[3]}")

                Log.i("NeuralNetwork", split_raw_nn[4])
                Log.i("NeuralNetwork", split_raw_nn[5])

                neuralNetwork.mapClassToFunction = convertRawStringToMap(split_raw_nn[4])
                Log.i("NeuralNetwork", neuralNetwork.mapClassToFunction.toString())
                neuralNetwork.mapFuntionToName = convertRawStringToMap(split_raw_nn[5])
                Log.i("NeuralNetwork", neuralNetwork.mapFuntionToName.toString())

                neuralNetwork.is_trained = true


                return neuralNetwork
            }
        }

        fun eraseSavedData(context: Context, filename: String = Parameters.FILENAME_MY_NN) {
            MyFileManager.writeToFile("", context, filename)
        }
    }


}