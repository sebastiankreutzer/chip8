package de.sebastiankreutzer.chip8

import java.awt.Graphics
import javax.swing.JPanel
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.sound.sampled.AudioSystem
import java.io.FileInputStream
import java.io.File
import java.io.BufferedInputStream
import javax.sound.sampled.FloatControl

class Screen extends JPanel(true) with Surface {

	val ScreenWidth = 64
	val ScreenHeight = 32

	val Black = 0x000000
	val White = 0xFFFFFF

	var scheme = new ColorScheme(0x000000, 0xFFFFFF, "Black & White")

	val img = new BufferedImage(ScreenWidth, ScreenHeight, BufferedImage.TYPE_INT_RGB)
	val data = img.getRaster().getDataBuffer().asInstanceOf[DataBufferInt].getData()

	override def drawSprite(x: Int, y: Int, sprite: Array[Byte]): Boolean = {
		//    println("Sprite at " + x + ", " + y)
		var pixelUnset = false
		for (i <- 0 until sprite.length) {
			for (j <- 0 to 7) {
				if (((sprite(i) & (1 << (7 - j))) >> (7 - j) & 1) == 1) {
					if (togglePixel(x + j, y + i))
						pixelUnset = true
				}
			}
		}
		repaint()
		pixelUnset
	}

	def togglePixel(x: Int, y: Int): Boolean = {
		var pixelUnset = false
		if (x >= 0 && y >= 0 && x < ScreenWidth && y < ScreenHeight) {
			if (data(y * ScreenWidth + x) == scheme.color2) {
				data(y * ScreenWidth + x) = scheme.color1
				pixelUnset = true
			} else {
				data(y * ScreenWidth + x) = scheme.color2
			}
		}
		pixelUnset
	}

	def setColorScheme(cs: ColorScheme) {
		scheme = cs
	}

	override def clear() {
		for (i <- 0 until data.length) data(i) = scheme.color1
		repaint()
	}

	override def paint(g: Graphics) {
		g.drawImage(img, 0, 0, getWidth, getHeight, this)
	}

	def drawDebug() {
		for (x <- 0 until ScreenWidth)
			printf("*")
		println()
		for (y <- 0 until ScreenHeight) {
			printf("*")
			for (x <- 0 until ScreenWidth) {
				if (data(y * ScreenWidth + x) == White)
					printf("-")
				else
					printf("x")
			}
			println("*")
		}
		for (x <- 0 until ScreenWidth)
			printf("*")
		println()
	}

	def playSound() {
		val clip = AudioSystem.getClip()
		val audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File("src/main/resources/sound.wav"))))
		clip.open(audioIn)
		val control = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
		control.setValue(-10.0f)
		clip.start
	}

}