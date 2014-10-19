package de.sebastiankreutzer.chip8

import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

import de.sebastiankreutzer.chip8.ColorScheme
import javax.swing.ButtonGroup
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JRadioButtonMenuItem
import javax.swing.KeyStroke

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

	val loadRomItem = new JMenuItem("Load ROM")
	loadRomItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			val fc = new JFileChooser(Main.configs.romDir)
			val result = fc.showOpenDialog(UI.this)
			if (result == JFileChooser.APPROVE_OPTION) {
				val file = fc.getSelectedFile()
				Main.loadRom(file)
			}
		}
	})
	fileMenu.add(loadRomItem)

	bar.add(fileMenu)

	val emulatorMenu = new JMenu("Emulator")

	//	val startItem = new JMenuItem("Start")
	//	startItem.addActionListener(new ActionListener() {
	//		override def actionPerformed(e: ActionEvent) {
	//			Main.startVM()
	//		}
	//	})
	//	emulatorMenu.add(startItem)
	//
	//	val stopItem = new JMenuItem("Stop")
	//	stopItem.addActionListener(new ActionListener() {
	//		override def actionPerformed(e: ActionEvent) {
	//			Main.stopVM()
	//		}
	//	})
	//	emulatorMenu.add(stopItem)

	emulatorMenu.addSeparator()

	val pauseItem = new JMenuItem("Pause")
	pauseItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			Main.pauseVM()
		}
	})
	emulatorMenu.add(pauseItem)

	val resumeItem = new JMenuItem("Resume")
	resumeItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			Main.resumeVM()
		}
	})
	emulatorMenu.add(resumeItem)

	emulatorMenu.addSeparator()

	val resetItem = new JMenuItem("Reset")
	resetItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			Main.resetRom()
		}
	})
	emulatorMenu.add(resetItem)

	emulatorMenu.addSeparator()

	bar.add(emulatorMenu)

	val freqMenu = new JMenu("Frequency")
	val freqGroup = new ButtonGroup()
	val freqs = Array(10, 50, 100, 200, 500, 1000, 5000, 10000)
	freqs.foreach(freq => {
		val freqItem = new JMenuItem(freq + " Hz")
		freqItem.addActionListener(new ActionListener() {
			override def actionPerformed(e: ActionEvent) {
				Main.setVMFrequency(freq)
			}
		})
		freqGroup.add(freqItem)
		freqMenu.add(freqItem)
	})

	emulatorMenu.add(freqMenu)

	val stateMenu = new JMenu("States")

	val loadItem = new JMenuItem("Load")
	loadItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			Main.loadState()
		}
	})
	loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0))
	stateMenu.add(loadItem)

	val saveItem = new JMenuItem("Save")
	saveItem.addActionListener(new ActionListener() {
		override def actionPerformed(e: ActionEvent) {
			Main.saveState()
		}
	})
	saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0))
	stateMenu.add(saveItem)

	val slotMenu = new JMenu("Select Slot")
	val slotGroup = new ButtonGroup()
	for (i <- 0 to 9) {
		val slotItem = new JRadioButtonMenuItem("Slot " + i)
		slotItem.addActionListener(new ActionListener() {
			override def actionPerformed(e: ActionEvent) {
				Main.selectSlot(i)
			}
		})
		slotItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(i + 48), 0))
		slotGroup.add(slotItem)
		slotMenu.add(slotItem)
	}
	stateMenu.add(slotMenu)

	bar.add(stateMenu)

	val viewMenu = new JMenu("View")

	val colorSchemes = Array(new ColorScheme(0x000000, 0xFFFFFF, "Black & White"),
		new ColorScheme(0x007F0E, 0x00FF21, "Green"),
		new ColorScheme(0x404040, 0xC0C0C0, "Gray"),
		new ColorScheme(0x7F92FF, 0x4800FF, "Blue"))

	val colorGroup = new ButtonGroup()
	colorSchemes.foreach(cs => {
		val colorItem = new JRadioButtonMenuItem(cs.name)
		colorItem.addActionListener(new ActionListener() {
			override def actionPerformed(e: ActionEvent) {
				screen.setColorScheme(cs)
			}
		})
		colorGroup.add(colorItem)
		viewMenu.add(colorItem)
	})

	bar.add(viewMenu)

	setJMenuBar(bar)

	screen.addKeyListener(this)
	screen.setFocusable(true)
	screen.requestFocusInWindow()

	def getScreen(): Screen = screen

	override def isKeyDown(key: Int): Boolean = keys(Main.configs.getKeyBinding(key))

	override def getPressedKey(): Int = {
		var key = -1
		for (i <- 0 to 15) {
			if (keys(Main.configs.getKeyBinding(i)))
				key = i
		}
		key
	}

	override def keyPressed(e: KeyEvent) = {
		keys(e.getKeyCode()) = true
		println("key " + Main.configs.getReverseKeyBinding(e.getKeyCode()) + " down (code=" + e.getKeyCode() + ")")
	}

	override def keyReleased(e: KeyEvent) = {
		keys(e.getKeyCode()) = false
	}

	override def keyTyped(e: KeyEvent) {}

}