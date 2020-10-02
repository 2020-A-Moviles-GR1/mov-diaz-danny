package com.example.projectappmobile.module_account.controler

import java.util.regex.Pattern

class LoginConnect {

    fun validateUser(user:String, pass:String): Boolean{
        /*
        ** Revisa la condición de la historia de usuario 6
        ** Retorna un Boolean dependiendo si el usuario y contraseña son correctos
        */

        // Revisa la longitud de las cadenas de texto
        if(user.length in 6..10 && pass.length in 6..12){

            // Revisa la solidez de la contraseña
            val flag : Boolean = Pattern.matches("^.*(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=]).*\$", pass)

            // Si la contraseña no cumple las mismas condiciones cuando se creó el usuario, entonces se evita peticiones a la base de datos
            if(flag){
                // Aquí en código de validación

                // Mientras no se implemente
                return true
            }else{
                return flag
            }
        } else {
            return false
        }
    }

}