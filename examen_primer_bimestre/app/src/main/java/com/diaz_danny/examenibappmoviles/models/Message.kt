package models

class Message(
    val sender: String,
    val message_id: Number,
    var content: String,
    var date_creation: String,
    var modified: Boolean = false,
    var ping_registered: Number = 50.6
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

    }
}