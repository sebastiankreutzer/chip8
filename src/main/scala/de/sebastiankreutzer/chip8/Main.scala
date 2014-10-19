package de.sebastiankreutzer.chip8

import java.io.File
import javax.swing.SwingUtilities
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

object Main extends App {

	val ConfigFile = new File("src/main/resources/configs.properties")
	val configs = new Configs(ConfigFile)

	var ui: UI = null
	var vm: Chip8VM = null
	
	var saveStates = new Array[VMState](10)
	var saveSlot = 0

	var rom: Option[Rom] = None

	SwingUtilities.invokeLater(new Runnable() {
		def run() {
			ui = new UI()
			ui.addWindowListener(new WindowAdapter() {
				override def windowClosing(e: WindowEvent) {
					println("Closing")
					configs.store()
				}
			})
			vm = new Chip8VM(ui.getScreen, ui)
			vm.frequency = configs.frequency
		}
	})

	val disassembler = new Dissasembler()
	//println(new File(".").getCanonicalPath())

	//  val rom = new Rom("roms/tetris.rom")
	//
	//  vm.loadRom(rom)
	//  vm.run()

//	def startVM() {
//		println("Starting VM")
//		vm.start()
//	}

	def pauseVM() {
		println("Pausing VM")
		vm.pause()
	}

	def resumeVM() {
		println("Resuming VM")
		vm.resume()
	}

//	def stopVM() {
//		println("Stoping VM")
//		vm.stop()
//	}

	def loadRom(file: File) {
		configs.romDir = file.getParentFile()
		rom = Some(new Rom(file.getPath()))
		resetRom()
	}

	def resetRom() {
		rom match {
			case None => println("No ROM loaded")
			case Some(rom) => vm.loadState(rom.reset)
		}
	}

	def setVMFrequency(frequency: Float) {
		vm.frequency = frequency
		configs.frequency = frequency
		println("Frequency set to " + frequency)
	}
	
	def selectSlot(slot: Int) {
		saveSlot = slot
	}
	
	def saveState() {
		saveStates(saveSlot) = new VMState(vm.state)
		println("Saved state " + saveSlot)
	}
	
	def loadState() {
		vm.loadState(saveStates(saveSlot))
		println("Loaded state " + saveSlot)
	}

}