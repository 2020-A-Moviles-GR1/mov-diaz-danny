package com.example.projectappmobile.models

import android.content.Context
import android.util.Log
import java.io.*

class MyFileManager {
    companion object {
        fun writeToFile(
            data: String,
            context: Context,
            filename: String
        ) {
            try {
                val fileOutputStream = context.openFileOutput(filename,Context.MODE_PRIVATE)

                val outputStreamWriter = OutputStreamWriter(
                    fileOutputStream
                )
                outputStreamWriter.write(data)
                outputStreamWriter.close()
            } catch (e: IOException) {
                Log.e("MyFileManager", "File write failed: " + e.toString())
            }
        }

        fun readFromFile(context: Context, filename: String): String? {
            var ret = ""
            try {
                val inputStream: InputStream? = context.openFileInput(filename)
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String? = ""
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also({ receiveString = it }) != null) {
                        stringBuilder.append(receiveString).append("\n")
                    }
                    inputStream.close()
                    ret = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                Log.e("MyFileManager", "File not found: " + e.toString())
            } catch (e: IOException) {
                Log.e("MyFileManager", "Can not read file: $e")
            }
            return ret
        }
    }
}