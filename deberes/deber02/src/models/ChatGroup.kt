package models

class ChatGroup(
    val chat_id: Number,
    var name_group: String,
    var date_creation: String,
    var active: Boolean = true,
    var mean_delay: Number = 50.6
) {


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


    companion object{
        var chats_created_counter = if(loadData(Parameters.dir_relative_files + Parameters.filename_chats_created) != "") loadData(Parameters.dir_relative_files + Parameters.filename_chats_created).toInt() else 0

        fun generateNewId(): Int{
            return chats_created_counter + 1
        }

        fun saveDataChatGroups(chat_groups: ArrayList<ChatGroup>){
            storeData(chatGroupsToRawString(chat_groups), Parameters.dir_relative_files + Parameters.filename_chat_groups)
            storeData(chats_created_counter.toString(), Parameters.dir_relative_files + Parameters.filename_chats_created)
        }

        fun loadDataChatGroups(): ArrayList<ChatGroup> {
            return rawStringToChatGroups(loadData(Parameters.dir_relative_files + Parameters.filename_chat_groups))
        }

        private fun rawStringToChatGroups(raw_chat_groups: String): ArrayList<ChatGroup>{

            val chat_groups: ArrayList<ChatGroup> = arrayListOf()

            raw_chat_groups.split(Parameters.SEPARATOR_OBJECTS).toTypedArray()
                .forEach {
                    val raw_chat_group = it.split(Parameters.SEPARATOR_ATTRIBUTES).toTypedArray()

                    var index = 0
                    val chat_id = raw_chat_group[index++].toInt()
                    val name_group = raw_chat_group[index++]
                    val date_creation = raw_chat_group[index++]
                    val active = raw_chat_group[index++].toBoolean()
                    val mean_delay = raw_chat_group[index++].toFloat()  // Siempre aumentar el índice si se agrega otro atributo

                    val chat_group = ChatGroup(chat_id, name_group, date_creation, active, mean_delay)

                    if(raw_chat_group[index] != "")
                        chat_group.messages = rawStringToMessages(raw_chat_group[index])
                    chat_groups.add(chat_group)
                }
            return chat_groups
        }

        private fun chatGroupsToRawString(chat_groups: ArrayList<ChatGroup>): String{

            var raw_chat_groups = ""

            chat_groups.forEachIndexed{ index, it ->
                raw_chat_groups += it.chat_id.toString() + Parameters.SEPARATOR_ATTRIBUTES
                raw_chat_groups += it.name_group + Parameters.SEPARATOR_ATTRIBUTES
                raw_chat_groups += it.date_creation + Parameters.SEPARATOR_ATTRIBUTES
                raw_chat_groups += it.active.toString() + Parameters.SEPARATOR_ATTRIBUTES
                raw_chat_groups += it.mean_delay.toString() + Parameters.SEPARATOR_ATTRIBUTES
                raw_chat_groups += messagesToRawString(it.messages)

                if(index < chat_groups.size - 1){
                    raw_chat_groups += Parameters.SEPARATOR_OBJECTS
                }
            }

            return raw_chat_groups
        }

        private fun rawStringToMessages(raw_messages: String): ArrayList<Message>{

            val messages: ArrayList<Message> = arrayListOf()

            raw_messages.split(Parameters.SEPARATOR_INTERNAL_OBJECT).toTypedArray()
                .forEach {


                    val message = it.split(Parameters.SEPARATOR_INTERNAL_OBJECT_ATTRIBUTES).toTypedArray()

                    var index = 0

                    val sender = message[index++]
                    val message_id = message[index++].toInt()
                    val content = message[index++]
                    val date_creation = message[index++]
                    val modified= message[index++].toBoolean()
                    val ping_registered = message[index].toFloat()  // Siempre aumentar el índice si se agrega otro atributo

                    messages.add(Message(sender, message_id, content, date_creation, modified, ping_registered))
                }
            return messages
        }

        private fun messagesToRawString(messages: ArrayList<Message>): String{

            var raw_messages = ""


            messages.forEachIndexed{ index, it ->
                raw_messages += it.sender + Parameters.SEPARATOR_INTERNAL_OBJECT_ATTRIBUTES
                raw_messages += it.message_id.toString() + Parameters.SEPARATOR_INTERNAL_OBJECT_ATTRIBUTES
                raw_messages += it.content + Parameters.SEPARATOR_INTERNAL_OBJECT_ATTRIBUTES
                raw_messages += it.date_creation + Parameters.SEPARATOR_INTERNAL_OBJECT_ATTRIBUTES
                raw_messages += it.modified.toString() + Parameters.SEPARATOR_INTERNAL_OBJECT_ATTRIBUTES
                raw_messages += it.ping_registered.toString()

                if(index < messages.size - 1){
                    raw_messages += Parameters.SEPARATOR_INTERNAL_OBJECT
                }
            }

            return raw_messages
        }

    }
}