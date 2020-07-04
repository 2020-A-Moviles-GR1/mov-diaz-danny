package views

import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JPanel

open class BaseController(
        var title: String,
        var panel: JPanel
){
    var frame: JFrame = JFrame(this.title)

    init {
        frame.contentPane = this.panel
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setLocationRelativeTo(null)
        frame.pack()
    }

    fun show(){
        this.frame.isVisible = true
    }

    fun hide(){
        this.frame.isVisible = false
    }

    fun close(){
        this.frame.dispatchEvent(WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING))
    }
}