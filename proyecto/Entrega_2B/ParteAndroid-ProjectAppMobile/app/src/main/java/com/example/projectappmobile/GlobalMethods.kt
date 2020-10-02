package com.example.projectappmobile

import android.content.Context
import android.widget.Toast

class GlobalMethods {
    companion object {
        fun showShortToast(context: Context, msg: String){
            if(!msg.equals("")){
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }

        fun showLongToast(context: Context, msg: String){
            if(!msg.equals("")){
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }
        


    }
}