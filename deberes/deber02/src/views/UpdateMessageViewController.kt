package views

import models.ChatGroup
import models.Message
import java.awt.event.WindowEvent
import java.lang.Math.random
import java.time.LocalDateTime
import java.util.*
import javax.swing.JOptionPane
import kotlin.system.exitProcess


class UpdateMessageViewController(
        title: String,
        private val parentViewController: MessagesViewController,
        private val chatGroup: ChatGroup,
        var message: Message,
        var chat_groups: ArrayList<ChatGroup>,
        private val updateMessageView: UpdateMessageView
):BaseController(title, updateMessageView.panel){

    init {

        this.updateMessageView.jLabelId.text = message.message_id.toString()
        this.updateMessageView.jLabelSender.text = message.sender
        this.updateMessageView.jLabelDate.text = message.date_creation
        this.updateMessageView.jLabelModified.text = if(message.modified) "Si" else "No"
        this.updateMessageView.textFieldContent.text = message.content

        parentViewController.hide()

        this.updateMessageView.buttonUpdate.addActionListener {

            val new_content: String = this.updateMessageView.textFieldContent.text

            if(new_content == ""){
                JOptionPane.showMessageDialog(null, "El mensaje no puede estar vacío")
                return@addActionListener
            }

            var index_chat_group = -1
            chat_groups.forEachIndexed{ index, it ->
                if(it.name_group == chatGroup.name_group){
                    index_chat_group = index
                }
            }

            var index_message = -1
            chatGroup.messages.forEachIndexed{ index, it ->
                if(it.message_id == message.message_id){
                    index_message = index
                }
            }

            if(chatGroup.messages[index_message].content != new_content){
                chatGroup.messages[index_message].content = new_content

                chatGroup.messages[index_message].modified = true

                chat_groups[index_chat_group] = chatGroup
                ChatGroup.saveDataChatGroups(chat_groups)
                parentViewController.updateMessagesList()
                parentViewController.show()
                this.hide()
            }else{
                JOptionPane.showMessageDialog(null, "No ha realizado ningun cambio al mensaje")
            }

        }

        this.updateMessageView.buttonClose.addActionListener {
            parentViewController.show()
            this.hide()
        }


    }


}