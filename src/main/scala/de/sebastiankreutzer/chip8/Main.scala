package de.sebastiankreutzer.chip8

object Main extends App {
 
  val ui = new UI()
  
  val vm = new Chip8VM(ui.getScreen)
  
  //println(new File(".").getCanonicalPath())
  
  val rom = new Rom("roms/breakout.rom")
  
  vm.loadRom(rom)
  vm.run()

}