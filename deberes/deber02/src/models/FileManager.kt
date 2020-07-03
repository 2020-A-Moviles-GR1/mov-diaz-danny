package models

import java.io.File
import java.io.FileNotFoundException

fun storeData(text: String, filename: String){
    val fileObject = File(filename)

    fileObject.createNewFile()
    if(!fileObject.exists()){
        fileObject.createNewFile()
    }
    fileObject.writeText(text)
}

fun loadData(filename: String): String {

    try {
        val file = File(filename)
        val bufferedReader = file.bufferedReader()
        val text:List<String> = bufferedReader.readLines()
        var output = ""
        text.forEachIndexed { index, it -> output = it + (if(index < text.size-1) "\n" else "") }
        return output
    }catch (error: FileNotFoundException){
        return ""
    }

}