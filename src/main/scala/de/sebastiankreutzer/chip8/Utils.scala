package de.sebastiankreutzer.chip8

object Utils {

  implicit class Chip8Byte(x: Byte) {
    def lo = x.toInt & 0xf
    def hi = (x.toInt & 0xf0) >> 4
    def toHexString = String.format("%02X", new java.lang.Integer(x & 0xff))
  }
  
  implicit class Chip8Int(x: Int) {
    def address = x & 0xFFF
  }
  
}