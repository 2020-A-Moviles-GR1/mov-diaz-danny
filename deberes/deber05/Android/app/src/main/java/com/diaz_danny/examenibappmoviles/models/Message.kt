package models

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon

class Message(
    val id: Int,
    val createdAt: Long,
    var updatedAt: Long,
    val sender: String,
    val message_id: Int,
    var content: String,
    var date_creation: String,
    var modified: Boolean = false,
    var ping_registered: Float = 50.6f,
    var chatGroup: Any?
) {


    init {
        messages_created_counter += 1
    }

    fun read() : String{
        return "INFORMACIÓN DEL MENSAJE\nId: ${this.message_id}\nEmisor: ${this.sender}\nFecha de creación ${this.date_creation}\nModificado: " + (if(this.modified) "si" else "no")+"\nPing registrado (ms): ${this.ping_registered}\nContenido: \n${this.content}\n"
    }

    override fun toString(): String {
        return "${this.sender}: " + (if(this.modified) "(Editado)" else "") + " ${this.content}"
    }

    companion object{
        var messages_created_counter = 0

        fun generateNewId(): Int{
            return messages_created_counter + 1
        }

        fun messagesToStringListNames(messages: ArrayList<Message>): Array<Any> {
            var listMessagesNames: ArrayList<Any> = arrayListOf()
            messages.forEach { listMessagesNames.add( it.message_id.toString() + "->" + it.sender + ": " + (if(it.modified) "(editado) " else "") + it.content) }
            return listMessagesNames.toTypedArray()
        }

        val myConverter = object: Converter {

            override fun canConvert(cls: Class<*>) = cls == Message::class.java

            override fun toJson(value: Any): String {

                val message = value as Message

                var chatGroup: Any?

                if(message.chatGroup is Int){
                    chatGroup = message.chatGroup
                }else if(message.chatGroup is ChatGroup){
                    chatGroup = Klaxon().toJsonString(message.chatGroup as ChatGroup)
                }else{
                    throw Error("No soportado")
                }

                return """
                  {

                    "id": ${message.id},
                    "createdAt": ${message.createdAt},
                    "updatedAt": ${message.updatedAt},
                    "sender": "${message.sender}",
                    "message_id": ${message.message_id},
                    "content": "${message.content}",
                    "date_creation": "${message.date_creation}",
                    "modified": ${message.modified},
                    "ping_registered": ${message.ping_registered},
                    "chatGroup": ${chatGroup}
                   }
                """.trimMargin()


            }



            override fun fromJson(jv: JsonValue) : Message {



                if(jv.obj?.get("chatGroup") is JsonObject){

                    return Message(
                        jv.objInt("id"),
                        jv.obj?.get("createdAt") as Long,
                        jv.obj?.get("updatedAt") as Long,
                        jv.objString("sender"),
                        jv.objInt("message_id"),
                        jv.objString("content"),
                        jv.objString("date_creation"),
                        jv.obj?.get("modified") as Boolean,
                        jv.obj?.get("ping_registered") as Float,
                        Klaxon().parseFromJsonObject<ChatGroup>(jv.obj?.get("chatGroup") as JsonObject)

                    )
                }else if(jv.obj?.get("chatGroup") is Int){
                    return Message(
                        jv.objInt("id"),
                        jv.obj?.get("createdAt") as Long,
                        jv.obj?.get("updatedAt") as Long,
                        jv.objString("sender"),
                        jv.objInt("message_id"),
                        jv.objString("content"),
                        jv.objString("date_creation"),
                        jv.obj?.get("modified") as Boolean,
                        jv.obj?.get("ping_registered") as Float,
                        jv.objInt("chatGroup")
                    )
                }else{
                    throw Error("No soportado")
                }


            }




        }


    }
}