package views

import models.ChatGroup
import models.Message
import java.awt.event.WindowEvent
import java.lang.Math.random
import java.time.LocalDateTime
import java.util.*
import javax.swing.JOptionPane
import kotlin.system.exitProcess


class UpdateChatGroupViewController(
        title: String,
        private val parentViewController: MainViewController,
        private val chatGroup: ChatGroup,
        private val updateChatGroupView: UpdateChatGroupView,
        var chat_groups: ArrayList<ChatGroup>
):BaseController(title, updateChatGroupView.panel){

    init {

        this.updateChatGroupView.jLabelChatGroupId.text = chatGroup.chat_id.toString()
        this.updateChatGroupView.textFieldChatGroupName.text = chatGroup.name_group
        this.updateChatGroupView.jLabelChatGroupDateCreation.text = chatGroup.date_creation
        this.updateChatGroupView.comboBoxIsActive.selectedIndex = if(chatGroup.active) 0 else 1
        this.updateChatGroupView.passwordField.text = chatGroup.password

        parentViewController.hide()

        this.updateChatGroupView.buttonUpdate.addActionListener {

            val chat_id: Number = this.updateChatGroupView.jLabelChatGroupId.text.toInt()
            val name_group: String = this.updateChatGroupView.textFieldChatGroupName.text
            val date_creation: String = this.updateChatGroupView.jLabelChatGroupDateCreation.text
            val active: Boolean = this.updateChatGroupView.comboBoxIsActive.selectedIndex == 0
            val mean_delay: Number = random()
            val password: String = this.updateChatGroupView.passwordField.text

            if(name_group == "" || password == "" ){
                JOptionPane.showMessageDialog(null, "Llene todos los campos")
                return@addActionListener
            }
            var index_chat_group = 10
            chat_groups.forEachIndexed{ index, it ->
                if(it.name_group == chatGroup.name_group){
                    index_chat_group = index
                }
            }
            chat_groups[index_chat_group] = ChatGroup(chat_id, name_group, date_creation, active, mean_delay, password)
            ChatGroup.saveDataChatGroups(chat_groups)
            parentViewController.updateChatGroupsList()
            parentViewController.show()
            this.hide()
        }

        this.updateChatGroupView.buttonCancel.addActionListener {
            parentViewController.show()
            this.hide()
        }


    }


}