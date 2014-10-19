package de.sebastiankreutzer.chip8

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.awt.event.KeyEvent
import java.io.FileOutputStream

class Configs(file: File) {

  var configs = new Properties()

  var keyBindings = Map[Int, Int]()
  var reverseKeyBindings = Map[Int, Int]()
  var frequency = 100.0f
  
  var romDir = new File(".")

  load()

  def load() {
    if (!file.exists()) {
      reset()
      store()
    }

    configs.load(new FileInputStream(file))

    for (i <- 0 to 15) {
      setKeyBinding(i, configs.getProperty("key" + i).toInt)
    }
    
    romDir = new File(configs.getProperty("romDir"))
    
    frequency = configs.getProperty("frequency").toFloat

    println(configs)
  }

  def store() {
    for (i <- 0 to 15) {
      configs.setProperty("key" + i, keyBindings(i).toString)
    }
    configs.setProperty("romDir", romDir.getPath)
    configs.setProperty("frequency", frequency.toString)
    configs.store(new FileOutputStream(file), "Config file for chip8 emulator")
  }

  def reset() {
    romDir = new File(".")
    setKeyBinding(0x0, KeyEvent.VK_A)
    setKeyBinding(0x1, KeyEvent.VK_S)
    setKeyBinding(0x2, KeyEvent.VK_D)
    setKeyBinding(0x3, KeyEvent.VK_F)
    setKeyBinding(0x4, KeyEvent.VK_G)
    setKeyBinding(0x5, KeyEvent.VK_H)
    setKeyBinding(0x6, KeyEvent.VK_J)
    setKeyBinding(0x7, KeyEvent.VK_K)
    setKeyBinding(0x8, KeyEvent.VK_L)
    setKeyBinding(0x9, KeyEvent.VK_Y)
    setKeyBinding(0xA, KeyEvent.VK_X)
    setKeyBinding(0xB, KeyEvent.VK_C)
    setKeyBinding(0xC, KeyEvent.VK_V)
    setKeyBinding(0xD, KeyEvent.VK_B)
    setKeyBinding(0xE, KeyEvent.VK_N)
    setKeyBinding(0xF, KeyEvent.VK_M)
  }

  def getKeyBinding(id: Int): Int = {
    if (keyBindings contains id)
      keyBindings(id)
    else KeyEvent.VK_UNDEFINED
  }

  def getReverseKeyBinding(keyCode: Int): Int = {
    if (reverseKeyBindings contains keyCode)
      reverseKeyBindings(keyCode)
    else
      -1
  }

  def setKeyBinding(id: Int, key: Int) {
    if (id >= 0 && id < 16) {
      keyBindings += (id -> key)
      reverseKeyBindings += (key -> id)
    }
  }

}