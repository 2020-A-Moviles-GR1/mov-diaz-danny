import models.*
import views.MainView
import java.awt.event.ActionListener
import java.lang.Math.random
import java.time.LocalDateTime
import java.util.*
import javax.swing.JFrame
import javax.swing.JOptionPane
import kotlin.reflect.typeOf

fun main(args:Array<String>) {

    /*


    val today = LocalDateTime.now().toString()
    val chat_with_message = ChatGroup(ChatGroup.generateNewId(), "MiChat", today, false, random())
    chat_with_message.addMessage(Message("Danny", Message.generateNewId(), "Hola, te escribo para decirte que...", today))

    val chat_with_messages = ChatGroup(ChatGroup.generateNewId(), "MiOtroChater", today, true, random())
    chat_with_messages.addMessage(Message("Jorge", Message.generateNewId(), "RESPUESTA A ESO...", today))
    chat_with_messages.addMessage(Message("Enrique", Message.generateNewId(), "No, es mas como un...", today))


    val chat_groups: ArrayList<ChatGroup> = arrayListOf(
        ChatGroup(ChatGroup.generateNewId(), "Grupito", today, true, random()),
        chat_with_message,
        chat_with_messages
    )
    ChatGroup.saveDataChatGroups(chat_groups)

    println(chat_groups.size)


    val chat_groups = ChatGroup.loadDataChatGroups()
    chat_groups.forEach { println(it.read()) }


    val chat_groups = ChatGroup.loadDataChatGroups()
    chat_groups.forEach {

        println(it.read());
        if(it.messages.size > 0){
            println(it.messages[0].read())
        }else{
            println("Sin mensajes")
        }
    }

     val mainView = MainView()

    val frame = JFrame("Ventana Principal")
    frame.contentPane = mainView.panel
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true

    mainView.buttonCreate.addActionListener(ActionListener { JOptionPane.showMessageDialog(null, "No me toques ahi")})


     */

    val mainView = MainView()

    val frame = JFrame("Ventana Principal")
    frame.contentPane = mainView.panel
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true

    mainView.buttonCreate.addActionListener(ActionListener { JOptionPane.showMessageDialog(null, "No me toques ahi")})






}