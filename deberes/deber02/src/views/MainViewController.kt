package views

import models.ChatGroup
import models.advancedCompareOfSearch
import java.util.ArrayList
import javax.swing.JOptionPane

class MainViewController(
        title: String,
        private val mainView: MainView,
        var chat_groups: ArrayList<ChatGroup>
):BaseController(title, mainView.panel){

    init {

        this.mainView.listGroupChats.setListData(ChatGroup.chatGroupsToStringListNames(chat_groups))

        this.mainView.buttonSearch.addActionListener {

            val search = this.mainView.textFieldSearch.text

            if(search == ""){
                JOptionPane.showMessageDialog(null, "Debe ingresar palabras de búsqueda primero primero")
                return@addActionListener
            }


            var index_search = -1
            this.chat_groups.forEachIndexed { index, chatGroup ->  if(advancedCompareOfSearch(chatGroup.name_group, search)) index_search = index;}
            if(index_search == -1){
                JOptionPane.showMessageDialog(null, "No se encontró ningun resultado")
            }else{
                this.mainView.listGroupChats.selectedIndex = index_search
                JOptionPane.showMessageDialog(null, "Se encontró un resultado y se lo seleccionó")
            }


        }

        this.mainView.buttonCreate.addActionListener {

            val createChatGroupViewController = CreateChatGroupController("Creación de grupos", this, ChatGroup.generateNewId(), CreateChatGroupView(), chat_groups)
            createChatGroupViewController.show()
        }

        this.mainView.buttonDetails.addActionListener {

            val primer_grupo_seleccionado = getValidSelectionFromList()
            if(primer_grupo_seleccionado != null){
                JOptionPane.showMessageDialog(null, primer_grupo_seleccionado.read())
            }

        }

        this.mainView.buttonUpdate.addActionListener {
            val first_group_selection = getValidSelectionFromList()
            if(first_group_selection != null){
                val updateChatGroupViewController = UpdateChatGroupViewController("Actualización de Grupo de chat", this, first_group_selection, UpdateChatGroupView(), chat_groups)
                updateChatGroupViewController.show()
            }
        }

        this.mainView.buttonDelete.addActionListener {
            val first_group_selection = getValidSelectionFromList()
            if(first_group_selection != null){

                if(JOptionPane.showConfirmDialog(null, "¿De verdad desea eliminar el elemento seleccionado?") == JOptionPane.YES_OPTION){
                    chat_groups.remove(first_group_selection)
                    ChatGroup.saveDataChatGroups(chat_groups)
                    updateChatGroupsList()
                }

            }

        }


        this.mainView.buttonViewMessages.addActionListener {

            val first_group_selection = getValidSelectionFromList()
            if(first_group_selection != null){

                var pass: String = JOptionPane.showInputDialog("Ingrese la contraseña de la sala de chat: ")
                if(pass == first_group_selection.password){
                    val messagesViewController = MessagesViewController("Mensajes del chat", this, MessagesView(), chat_groups, first_group_selection)
                    messagesViewController.show()
                }else{
                    JOptionPane.showMessageDialog(null, "Contraseña incorrecta")
                }
            }
        }


    }

    fun updateChatGroupsList() {
        this.mainView.listGroupChats.setListData(ChatGroup.chatGroupsToStringListNames(chat_groups))
    }

    private fun getValidSelectionFromList(): ChatGroup? {
        /* Regresa el primer grupo que encuentra de acuerdo al nombre mostrado*/
        val selectedValue = this.mainView.listGroupChats.selectedValue

        if(selectedValue != null){
            return this.chat_groups.filter { it.name_group == selectedValue }[0]
        }else{
            JOptionPane.showMessageDialog(null, "Primero debe seleccionar un elemento de la lista!\nSi no hay ningun elemento, cree uno")
            return null
        }
    }

}