package de.sebastiankreutzer.chip8

import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

class UI extends JFrame with InputProcessor with KeyListener {

	val keys = new Array[Boolean](1024)

	val Size = new Dimension(800, 600)
	val screen = new Screen()

	setTitle("Scala8")
	setVisible(true)
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	screen.setPreferredSize(Size)
	screen.setMinimumSize(Size)
	screen.setMaximumSize(Size)
	add(screen)
	pack()
	setLocationRelativeTo(null)

	val bar = new JMenuBar()

	val fileMenu = new JMenu("File")
	val loadItem = new JMenuItem("Load ROM")
	loadItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			val fc = new JFileChooser(Main.configs.getRomDir())
			val result = fc.showOpenDialog(UI.this)
			if (result == JFileChooser.APPROVE_OPTION) {
				val file = fc.getSelectedFile()
				Main.loadRom(file)
			}
		}
	})
	fileMenu.add(loadItem)
	bar.add(fileMenu)

	setJMenuBar(bar)

	screen.addKeyListener(this)
	screen.setFocusable(true)
	screen.requestFocusInWindow()

	def getScreen(): Screen = screen

	override def isKeyDown(key: Int): Boolean = keys(Main.configs.getKeyBinding(key))

	override def keyPressed(e: KeyEvent) = {
		keys(e.getKeyCode()) = true
		println("key " + Main.configs.getReverseKeyBinding(e.getKeyCode()) + " down (code=" + e.getKeyCode() + ")")
	}

	override def keyReleased(e: KeyEvent) = {
		keys(e.getKeyCode()) = false
	}

	override def keyTyped(e: KeyEvent) {}

}