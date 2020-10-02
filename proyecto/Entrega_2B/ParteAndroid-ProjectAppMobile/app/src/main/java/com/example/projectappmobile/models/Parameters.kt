package com.example.projectappmobile.models

class Parameters {
    companion object {
        val WAIT_TIME_FOR_NEXT_PREDICTION_S: Byte = 2;
        val MAX_SAMPLES_SAVED_FOR_TRAINING: Byte = 10;
        var THRESHOLD_GESTURE_MARGIN_SIZE_POSIBILITY: Byte = 30;
        var THRESHOLD_GESTURE_WINDOW_SIZE_POSIBILITY: Byte = 2;
        var THRESHOLD_GESTURE_MEAN_VELOCITY_POSIBILITY: Byte = 5;

        val FILENAME_MY_NN: String = "neural_network_config.txt"
        val FILENAME_MY_ID_FUNCTIONS_SAVED: String = "my_id_functions.txt"

        var IP_SERVER = "192.168.1.6"
        var BASE_URL_BACKEND_API: String = "http://$IP_SERVER"  // ip de la máquina server o dominio.com, etc
        var WS_SERVER_PATH: String = "ws://$IP_SERVER:8080"

        const val API_ENDPOINT: String = "mov-gestures/backend/public_html/api/"  // en servidores realales solo iría "api/", dado que lo demás es ignorado, para pruebas en local se usa todo el directorio
        const val API_ENDPOINT_GATE: String = "mov-gestures/backend/public_html/api_gate.php"

        val WAIT_TIME_S: Int = 1
        val INVALID_SENSOR_VALUE: Float = -999.0f
        val GESTURE_DURATION_MS: Int = 1280
        val GESTURE_NUMBER_MEASURES: Int = 100
        val TIME_PER_GESTURE_MEASURE_MS: Int = GESTURE_DURATION_MS / GESTURE_NUMBER_MEASURES


    }
}