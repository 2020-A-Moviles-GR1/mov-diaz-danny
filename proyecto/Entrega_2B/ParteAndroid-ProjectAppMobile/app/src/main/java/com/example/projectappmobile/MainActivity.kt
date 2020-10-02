package com.example.projectappmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.projectappmobile.module_account.controler.LoginConnect
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projectappmobile.models.ConstantParameters
import com.example.projectappmobile.models.Criptografia
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.MyFileManager
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.models.retrofit.ApiClient
import com.example.projectappmobile.models.retrofit.ApiInterface
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataString
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataUser
import com.example.projectappmobile.module_account.view.RegisterActivity
import com.example.projectappmobile.module_gestures.MyGesturesActivity
import com.example.projectappmobile.module_gestures.interactor.AddGestureInteractor
import com.example.projectappmobile.module_home.HomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val lgConnet : LoginConnect = LoginConnect()

    companion object {
        val TAG = "MainActivity";
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLoginLogin.setOnClickListener {
            val username = editTextLoginUsername.text.toString();
            val password = editTextLoginPassword.text.toString();
            if(!validateLogin(username, password)){
                GlobalMethods.showShortToast(baseContext, "Ingrese todos los datos")
                return@setOnClickListener
            }

            val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
                ApiInterface::class.java
            )

            val map = HashMap<String, Any>()
            map.put("route", "users")
            map.put("filename", "auth")
            map.put("username", username)
            map.put("password", Criptografia.get_SHA_256_SecurePassword(password))

            Log.d(AddGestureInteractor.TAG, map.get("samples").toString())

            val call: Call<MyServerResponseDataUser>? = apiInterface?.authUser(map)
            call?.enqueue(object : Callback<MyServerResponseDataUser?> {
                override fun onResponse(
                    call: Call<MyServerResponseDataUser?>,
                    response: Response<MyServerResponseDataUser?>
                ) {

                    Log.d(MyGesturesActivity.TAG, "onResponse: buttonLoginLogin: " + response.body())

                    if(response.body() != null){
                        if(response.body()!!.id_message == 1){

                            val metadata = response.body()!!.metadata?.toInt();

                            if(metadata == 0){
                                GlobalMethods.showShortToast(baseContext, "Credenciales inválidas")
                            }
                            else if(metadata == 1){
                                User.loggedUser =
                                    response.body()!!.data?.get(0)?.full_name?.let { it1 ->
                                        User(username,
                                            it1
                                        )
                                    }
                                val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
                                with (sharedPref.edit()) {
                                    putString("username", username)
                                    putString("full_name",
                                        response.body()!!.data?.get(0)?.full_name
                                    )
                                    commit()
                                }

                                navigateHome()
                            }



                        }else{
                            GlobalMethods.showShortToast(baseContext, "Credenciales inválidas")
                        }
                    }else{
                        GlobalMethods.showShortToast(baseContext, "Ocurrió un error de conexión")
                    }
                }

                override fun onFailure(call: Call<MyServerResponseDataUser?>, t: Throwable) {
                    GlobalMethods.showShortToast(baseContext, ErrorHandler.euphemiseMessage(t))
                    Log.e(TAG, "buttonLoginLogin: $t")
                }
            })


        }

        buttonLoginRegister.setOnClickListener {
            val intentExplicit = Intent(this,  RegisterActivity::class.java)
            startActivityForResult(intentExplicit, ConstantParameters.REQUEST_CODE_REGISTER)
        }

    }

    private fun navigateHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivityForResult(intent, ConstantParameters.REQUEST_CODE_HOME)
    }

    private fun validateLogin(user:String, pass:String): Boolean{

        return user != "" && pass != "";
    }

    override fun onResume() {
        super.onResume()

        if(User.getLoggedIngUser() == null){
            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
            val username = sharedPref.getString("username", null)
            val full_name = sharedPref.getString("full_name", null)
            if(username.equals("") || username == null || full_name.equals("") || full_name == null){
                // no hay usuario
                Log.i(TAG, "No hay ningun inicio de sesión previo")
            }else{
                User.loggedUser = User(username, full_name)
                navigateHome()
            }
        }





    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){

            when (requestCode) {
                ConstantParameters.REQUEST_CODE_HOME -> {

                }
            }


        }else if(resultCode == Activity.RESULT_CANCELED){

            when (requestCode) {
                ConstantParameters.REQUEST_CODE_HOME -> {

                }
            }

        }

    }


    fun showShortToast(msg: String){
        if(!msg.equals("")){
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }


}