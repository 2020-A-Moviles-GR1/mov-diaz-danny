package models

import java.io.Serializable
import java.security.MessageDigest


class ChatGroup(
    val createdAt: Long,
    var updatedAt: Long,
    val id: Int,
    val chat_id: Int,
    var name_group: String,
    var date_creation: String,
    var active: Boolean = true,
    var mean_delay: Float = 50.6f,
    var password: String = "root123",
    var messages: ArrayList<Message>? = null
): Serializable {




    init {
        messages = arrayListOf()
    }

    fun read() : String{
        return "\nId: ${this.chat_id}\nNombre: ${this.name_group}\nFecha de creación ${this.date_creation}\nActivo: " + (if(this.active) "si" else "no")+"\nRetraso de envio promedio (ms): ${this.mean_delay}"
    }

    fun addMessage(message: Message){
        this.messages?.add(message)
    }

    fun removeMessage(message: Message){
        this.messages?.remove(message)
    }

    override fun toString(): String {
        return this.name_group
    }


    companion object{
        var companion_chat_groups = ArrayList<ChatGroup>()

        fun generateNewId(): Int{
            return companion_chat_groups.size + 1
        }

        fun chatGroupsToStringListNames(chatGroups: ArrayList<ChatGroup>): Array<Any> {
            var listChatGroupsNames: ArrayList<Any> = arrayListOf()
            chatGroups.forEach { listChatGroupsNames.add(it.name_group) }
            return listChatGroupsNames.toTypedArray()
        }

    }
}