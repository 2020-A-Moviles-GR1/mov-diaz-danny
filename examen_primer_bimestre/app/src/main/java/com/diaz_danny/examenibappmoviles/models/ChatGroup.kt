package models

import java.io.Serializable
import java.security.MessageDigest


class ChatGroup(
    val chat_id: Number,
    var name_group: String,
    var date_creation: String,
    var active: Boolean = true,
    var mean_delay: Number = 50.6,
    var password: String = "root123"
): Serializable {


    var messages: ArrayList<Message>

    init {
        chats_created_counter += 1
        messages = arrayListOf()
    }

    fun read() : String{
        return "\nId: ${this.chat_id}\nNombre: ${this.name_group}\nFecha de creación ${this.date_creation}\nActivo: " + (if(this.active) "si" else "no")+"\nRetraso de envio promedio (ms): ${this.mean_delay}"
    }

    fun addMessage(message: Message){
        this.messages.add(message)
    }

    fun removeMessage(message: Message){
        this.messages.remove(message)
    }

    override fun toString(): String {
        return this.name_group
    }


    companion object{
        var chats_created_counter = 0
        var companion_chat_groups = ArrayList<ChatGroup>()

        fun generateNewId(): Int{
            return chats_created_counter + 1
        }

        fun chatGroupsToStringListNames(chatGroups: ArrayList<ChatGroup>): Array<Any> {
            var listChatGroupsNames: ArrayList<Any> = arrayListOf()
            chatGroups.forEach { listChatGroupsNames.add(it.name_group) }
            return listChatGroupsNames.toTypedArray()
        }

    }
}