package de.sebastiankreutzer.chip8

import java.awt.Image
import java.io.BufferedReader
import java.io.InputStreamReader
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.DataBufferInt
import java.awt.image.DataBufferByte

object ScreenCreator extends App {

	val reader = new BufferedReader(new InputStreamReader(System.in))

	println("Screen Creator for Emul8")
	println("************************")

	println()

	println("Enter image location:")

	val path = "C:/Users/Sebastian/Desktop/splash.png"

	println()

	val img = ImageIO.read(new File(path))

	for (y <- 0 until img.getHeight) {
		for (x <- 0 until img.getWidth) {
			val pixel = img.getRGB(x, y)
			print(if (pixel == 0xFFFFFFFF) 1 else 0)
		}
	}

}