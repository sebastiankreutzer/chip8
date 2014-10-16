package de.sebastiankreutzer.chip8

import java.io.File

import javax.swing.SwingUtilities

object Main extends App {

  val ConfigFile = new File("src/main/resources/configs.properties")
  val configs = new Configs(ConfigFile)

  var ui: UI = null
  var vm: Chip8VM = null

  SwingUtilities.invokeLater(new Runnable() {
    def run() {
      ui = new UI()
      vm = new Chip8VM(ui.getScreen, ui)
    }
  })

  val disassembler = new Dissasembler()
  //println(new File(".").getCanonicalPath())

  //  val rom = new Rom("roms/tetris.rom")
  //
  //  vm.loadRom(rom)
  //  vm.run()

  def loadRom(file: File) {
    new Thread() {
      override def run() {
        configs.setRomDir(file.getParentFile())
        val rom = new Rom(file.getPath())
        vm.loadRom(rom)
        println("xxxxxxxxxxxxxxxxxxxxxxxxxx")
//        println(disassembler.dissasemble(rom))
        println("xxxxxxxxxxxxxxxxxxxxxxxxxx")
        vm.run()
      }
    }.start()
  }

}