package com.example.projectappmobile.module_account.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.projectappmobile.GlobalMethods
import com.example.projectappmobile.MainActivity
import com.example.projectappmobile.R
import com.example.projectappmobile.models.ConstantParameters
import com.example.projectappmobile.models.Criptografia
import com.example.projectappmobile.models.ErrorHandler
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.models.retrofit.ApiClient
import com.example.projectappmobile.models.retrofit.ApiInterface
import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataUser
import com.example.projectappmobile.module_gestures.MyGesturesActivity
import com.example.projectappmobile.module_gestures.interactor.AddGestureInteractor
import com.example.projectappmobile.module_home.HomeActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonRegister.setOnClickListener {
            val username = editTextRegisterUsername.text.toString();
            val password = editTextRegisterPassword.text.toString();
            val password_confirm = editTextRegisterPasswordConfirm.text.toString();
            val full_name = editTextRegisterFullName.text.toString();

            if(!validateLogin(username, password, password_confirm, full_name)){
                GlobalMethods.showShortToast(baseContext, "Ingrese todos los datos")
                return@setOnClickListener
            }

            val apiInterface: ApiInterface? = ApiClient.getApiClient()?.create(
                ApiInterface::class.java
            )

            val map = HashMap<String, Any>()
            map.put("route", "users")
            map.put("filename", "create")
            map.put("username", username)
            map.put("password", Criptografia.get_SHA_256_SecurePassword(password))
            map.put("full_name", full_name)

            Log.d(AddGestureInteractor.TAG, map.get("samples").toString())

            val call: Call<MyServerResponseDataUser>? = apiInterface?.createUser(map)
            call?.enqueue(object : Callback<MyServerResponseDataUser?> {
                override fun onResponse(
                    call: Call<MyServerResponseDataUser?>,
                    response: Response<MyServerResponseDataUser?>
                ) {

                    Log.d(MyGesturesActivity.TAG, "onResponse: buttonLoginLogin: " + response.body())

                    if(response.body() != null){
                        if(response.body()!!.id_message == 1){
                            User.loggedUser = User(username, full_name)
                            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
                            with (sharedPref.edit()) {
                                putString("username", username)
                                putString("full_name",
                                    full_name
                                )
                                commit()
                            }
                            navigateHome()
                        }else{
                            GlobalMethods.showShortToast(baseContext, "Credenciales inválidas")
                        }
                    }else{
                        GlobalMethods.showShortToast(baseContext, "Ocurrió un error de conexión")
                    }
                }

                override fun onFailure(call: Call<MyServerResponseDataUser?>, t: Throwable) {
                    GlobalMethods.showShortToast(baseContext, ErrorHandler.euphemiseMessage(t))
                    Log.e(MainActivity.TAG, "buttonLoginLogin: $t")
                }
            })
        }
    }

    private fun navigateHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivityForResult(intent, ConstantParameters.REQUEST_CODE_HOME)
    }

    private fun validateLogin(
        username: String,
        password: String,
        passwordConfirm: String,
        fullName: String
    ): Boolean {
        return !username.equals("") && !password.equals("") && !passwordConfirm.equals("") && !fullName.equals("")
    }
}