package de.sebastiankreutzer.chip8

trait InputProcessor {
  
  def isKeyDown(key : Int): Boolean

}