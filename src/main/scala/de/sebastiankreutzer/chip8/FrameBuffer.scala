package de.sebastiankreutzer.chip8

class FrameBuffer(w: Int, h: Int) {

	val data = new Array[Boolean](w * h)

	def this(fb: FrameBuffer) {
		this(fb.width, fb.height)
		System.arraycopy(fb.data, 0, this.data, 0, fb.data.length)
	}

	def width: Int = w
	def height: Int = h

	def togglePixel(x: Int, y: Int): Boolean = {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			val i = y * width + x
			data(i) = !data(i)
			!data(i)
		} else false
	}

	def drawSprite(x: Int, y: Int, sprite: Array[Byte]): Boolean = {
		var pixelUnset = false
		for (i <- 0 until sprite.length) {
			for (j <- 0 to 7) {
				if (((sprite(i) & (1 << (7 - j))) >> (7 - j) & 1) == 1) {
					if (togglePixel(x + j, y + i))
						pixelUnset = true
				}
			}
		}
		pixelUnset
	}

	def clear() {
		(0 until data.length) foreach (i => data(i) = false)
	}

}