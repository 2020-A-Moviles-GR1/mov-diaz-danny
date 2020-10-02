package com.example.projectappmobile.models.retrofit

import com.example.projectappmobile.models.Parameters
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataFunction
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataGesture
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataString
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataUser
import retrofit2.Call
import retrofit2.http.*
import kotlin.collections.HashMap as HashMap


public interface ApiInterface {


    @POST(Parameters.API_ENDPOINT_GATE)
    fun authUser(@Body map: HashMap<String, Any>): Call<MyServerResponseDataUser>

    @POST(Parameters.API_ENDPOINT_GATE)
    fun createUser(@Body map: HashMap<String, Any>): Call<MyServerResponseDataUser>



    @POST("${Parameters.API_ENDPOINT}module_gestures/insert_gesture.php")
    fun createGesture(@Body map: HashMap<String, Any>): Call<MyServerResponseDataString>

    @POST("${Parameters.API_ENDPOINT}module_gestures/insert_sample.php")
    fun uploadSampleForGesture(@Body map: HashMap<String, Any>): Call<MyServerResponseDataString>

    @GET("${Parameters.API_ENDPOINT}module_gestures/get_functions.php")
    fun getFunctions(): Call<MyServerResponseDataFunction>?


    @GET("${Parameters.API_ENDPOINT}module_gestures/get_functions.php")
    fun getFunctions(@Query("option") option: Int): Call<MyServerResponseDataFunction>?


    @POST("${Parameters.API_ENDPOINT}module_gestures/get_my_gestures.php")
    fun getMyGestures(@Body map: HashMap<String, Any>): Call<MyServerResponseDataGesture>?


    @POST("${Parameters.API_ENDPOINT}module_gestures/delete_gesture.php")
    fun deleteGesture(@Body map: java.util.HashMap<String, Any>): Call<MyServerResponseDataGesture>?

    @POST("${Parameters.API_ENDPOINT}module_gestures/insert_gesture_with_samples.php")
    fun insertGestureWithSamples(@Body map: java.util.HashMap<String, Any>): Call<MyServerResponseDataString>?

    @POST("${Parameters.API_ENDPOINT}module_gestures/update_gesture_with_samples.php")
    fun updateGestureWithSamples(@Body map: java.util.HashMap<String, Any>): Call<MyServerResponseDataString>?


    /*
    @FormUrlEncoded
    @POST(Parametros.RUTA_ANDROID_DELIVER.toString() + "deliver_android_cambiar_estado_pedido.php")
    fun cambiarEstadoPedido(
        @Field("pedido_id") pedido_id: Int,
        @Field("estado") nuevo_estado: Int
    ): Call<Respuesta?>?

    @POST(Parametros.ENDPOINT_GOOGLE_API_FCM)
    fun sendPushNotificationToToken(
        @Header("Accept") accept: String?,
        @Header("Authorization") auth: String?,
        @Body payload: PushNotification?
    ): Call<RespuestaAPI?>?

     @GET(Parametros.RUTA_ANDROID_DELIVER.toString() + "deliver_android_consultar_splash_screen.php")
    fun obtenerImagenSplashScreen(): Call<Imagen?>?
    * */
}