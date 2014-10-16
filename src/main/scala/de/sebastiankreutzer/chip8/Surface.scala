package de.sebastiankreutzer.chip8

trait Surface {
  
  def drawSprite(x : Int, y: Int, sprite : Array[Byte]) : Boolean

  def clear()
  
}