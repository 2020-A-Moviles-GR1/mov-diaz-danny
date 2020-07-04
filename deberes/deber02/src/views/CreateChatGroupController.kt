package views

import models.ChatGroup
import models.Message
import java.awt.event.WindowEvent
import java.lang.Math.random
import java.time.LocalDateTime
import java.util.*
import javax.swing.JOptionPane
import kotlin.system.exitProcess


class CreateChatGroupController(
        title: String,
        private val parentViewController: MainViewController,
        private val generatedId: Int,
        private val createChatGroupView: CreateChatGroupView,
        var chat_groups: ArrayList<ChatGroup>
):BaseController(title, createChatGroupView.panel){

    init {

        val today = LocalDateTime.now().toString()
        this.createChatGroupView.jLabelChatGroupId.text = generatedId.toString()
        this.createChatGroupView.jLabelChatGroupDateCreation.text = today
        parentViewController.hide()

        this.createChatGroupView.buttonCreate.addActionListener {



            val chat_id: Number = this.generatedId
            val name_group: String = this.createChatGroupView.textFieldChatGroupName.text
            val date_creation: String = this.createChatGroupView.jLabelChatGroupDateCreation.text
            val active: Boolean = this.createChatGroupView.comboBoxIsActive.selectedIndex == 0
            val mean_delay: Number = random()
            val password: String = this.createChatGroupView.passwordField.text

            if(name_group == "" || password == "" ){
                JOptionPane.showMessageDialog(null, "Llene todos los campos")
                return@addActionListener;
            }

            chat_groups.add(ChatGroup(chat_id, name_group, date_creation, active, mean_delay, password))
            ChatGroup.saveDataChatGroups(chat_groups)
            parentViewController.updateChatGroupsList()
            parentViewController.show()
            this.hide()
        }

        this.createChatGroupView.buttonCancel.addActionListener {
            parentViewController.show()
            this.hide()


        }


    }


}