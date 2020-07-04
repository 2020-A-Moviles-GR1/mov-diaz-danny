package views

import models.ChatGroup
import models.Message
import models.advancedCompareOfSearch
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.lang.Math.random
import java.time.LocalDateTime
import java.util.*
import javax.swing.JOptionPane


class MessagesViewController(
        title: String,
        private val parentView: MainViewController,
        private val messagesView: MessagesView,
        var chat_groups: ArrayList<ChatGroup>,
        val chatGroup: ChatGroup
):BaseController(title, messagesView.panel){

    init {

        this.messagesView.listMessages.setListData(Message.messagesToStringListNames(chatGroup.messages))
        this.parentView.hide()

        val keyListener: KeyListener = object : KeyListener {
            override fun keyPressed(keyEvent: KeyEvent) {
                //printIt("Pressed", keyEvent)
            }

            override fun keyReleased(keyEvent: KeyEvent) {
                //printIt("Released", keyEvent)
                if(KeyEvent.getKeyText(keyEvent.keyCode) == "Enter"){
                    sendMessage()
                }
            }

            override fun keyTyped(keyEvent: KeyEvent) {
                //printIt("Typed", keyEvent)
            }

            private fun printIt(title: String, keyEvent: KeyEvent) {
                val keyCode = keyEvent.keyCode
                val keyText = KeyEvent.getKeyText(keyCode)
                println(title + " : " + keyText + " / " + keyEvent.keyChar)
            }
        }

        this.messagesView.textFieldMessageContent.addKeyListener(keyListener)

        this.messagesView.buttonSearch.addActionListener {

            val search = this.messagesView.textFieldSearch.text

            if(search == ""){
                JOptionPane.showMessageDialog(null, "Debe ingresar palabras de búsqueda primero primero")
                return@addActionListener
            }


            var index_search = -1
            this.chatGroup.messages.forEachIndexed { index, message ->  if(advancedCompareOfSearch(message.content, search)) index_search = index;}
            if(index_search == -1){
                JOptionPane.showMessageDialog(null, "No se encontró ningun resultado")
            }else{
                this.messagesView.listMessages.selectedIndex = index_search
                JOptionPane.showMessageDialog(null, "Se encontró un resultado y se lo seleccionó")
            }


        }

        this.messagesView.buttonCreate.addActionListener {


            sendMessage()

        }

        this.messagesView.buttonDetails.addActionListener {

            val primer_grupo_seleccionado = getValidSelectionFromList()
            if(primer_grupo_seleccionado != null){
                JOptionPane.showMessageDialog(null, primer_grupo_seleccionado.read())
            }

        }

        this.messagesView.buttonUpdate.addActionListener {
            val first_group_selection = getValidSelectionFromList()
            if(first_group_selection != null){
                val updateMessageViewController = UpdateMessageViewController("Actualización de Mensaje", this, chatGroup, first_group_selection, chat_groups, UpdateMessageView())
                updateMessageViewController.show()

            }
        }

        this.messagesView.buttonDelete.addActionListener {
            val primer_grupo_seleccionado = getValidSelectionFromList()
            if(primer_grupo_seleccionado != null){

                if(JOptionPane.showConfirmDialog(null, "¿De verdad desea eliminar el elemento seleccionado?") == JOptionPane.YES_OPTION)
                {
                    chatGroup.messages.remove(primer_grupo_seleccionado)
                    var index_chat_group = -1
                    chat_groups.forEachIndexed{ index, group ->
                        if(group.name_group == chatGroup.name_group){
                            index_chat_group = index
                        }
                    }
                    chat_groups[index_chat_group] = chatGroup  // refresh
                    ChatGroup.saveDataChatGroups(chat_groups)
                    updateMessagesList()
                }

            }

        }


        this.messagesView.buttonClose.addActionListener {
            parentView.show()
            this.hide()

        }


    }

    private fun sendMessage() {
        val sender: String? = this.messagesView.comboBox1.selectedItem?.toString()
        val message_id: Number = Message.generateNewId()
        val content: String = this.messagesView.textFieldMessageContent.text
        val date_creation: String = LocalDateTime.now().toString()
        val modified = false
        val ping_registered: Number = random() * 200

        if(sender == null || content == "" ){
            JOptionPane.showMessageDialog(null, "Escriba el mensaje")
            return
        }

        var index_chat_group = -1
        chat_groups.forEachIndexed{ index, group ->
            if(group.name_group == chatGroup.name_group){
                index_chat_group = index
            }
        }

        chatGroup.addMessage(Message(sender, message_id, content, date_creation, modified, ping_registered))
        chat_groups[index_chat_group] = chatGroup  // refresh
        ChatGroup.saveDataChatGroups(chat_groups)
        updateMessagesList()

        this.messagesView.textFieldMessageContent.text = ""
    }

    fun updateMessagesList() {
        this.messagesView.listMessages.setListData(Message.messagesToStringListNames(chatGroup.messages))
    }

    private fun getValidSelectionFromList(): Message? {
        /* Regresa el primer grupo que encuentra de acuerdo al nombre mostrado*/
        val selectedValue = this.messagesView.listMessages.selectedValue

        if(selectedValue != null){
            val selected_id = selectedValue.toString().split("->")[0]
            return this.chatGroup.messages.filter { it.message_id.toString() == selected_id }[0]
        }else{
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un elemento de la lista!\nSi no hay ningun elemento, cree uno")
            return null
        }
    }

}