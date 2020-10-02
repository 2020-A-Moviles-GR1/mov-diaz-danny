package com.example.projectappmobile.models

class ErrorHandler {

    companion object {

        fun euphemiseMessage(t: Throwable) : String{
            return t.toString()  // a un usuario no se le debe mostrar este mensaje o es un fallo de seguridad
        }
    }

}