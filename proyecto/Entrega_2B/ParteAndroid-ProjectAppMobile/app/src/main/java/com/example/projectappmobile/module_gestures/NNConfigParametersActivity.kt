package com.example.projectappmobile.module_gestures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectappmobile.R

class NNConfigParametersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_n_n_config_parameters)



    }


    fun updateConfig(){

        /// Basic ///
        val epochs: Int = 5
        val alpha: Double = 0.1
        val lambda: Double = 0.0
        val normalize: Boolean = true
        val shuffle: Boolean = true
        val verbose: Boolean = true
        var neurons_per_layer: IntArray = intArrayOf(-1, 35, 18, -1)

        ///DATA///
        // Fake data generation
        val augmentation_by_shifting_percent_of_data: Double = 0.05
        val augmentation_by_move_average_box_begin: Int = 12
        val augmentation_by_move_average_box_end: Int = 20

        // Data selection
        val samples: Int = -1
        val train_split_portion: Double = 0.7
        val validation_split_portion: Double = 0.15

        /// Advanced ///
        val random_seed_shuffle: Long = 42
        val random_seed_init_weights: Long = 24
        val std_objective_init_weights: Double = 1.0
        val mean_objective_init_weights: Double = 0.0
    }
}