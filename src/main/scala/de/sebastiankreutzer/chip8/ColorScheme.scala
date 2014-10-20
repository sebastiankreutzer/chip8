package de.sebastiankreutzer.chip8

class ColorScheme(c1: Int, c2: Int, n: String) {
	def color1: Int = c1
	def color2: Int = c2
	def name: String = n
}

object ColorScheme {

	val BlackAndWhite = new ColorScheme(0x000000, 0xFFFFFF, "Black & White")
	val C64 = new ColorScheme(0x4040E0, 0xA0A1FE, "C64")
	val Gray = new ColorScheme(0x404040, 0xC0C0C0, "Gray")
	val Green = new ColorScheme(0x103810, 0x10B010, "Green")
	
	val All = Array(BlackAndWhite, C64, Gray, Green)
	
}