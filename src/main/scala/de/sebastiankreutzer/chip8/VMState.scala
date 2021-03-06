package de.sebastiankreutzer.chip8

class VMState {
	
	val ram = new Array[Byte](4096)

	var pc = 0x200

	val registers = new Array[Byte](16)

	var i = 0

	val stack = new Array[Int](16)
	var sp = 0

	var delayTimer = 0
	var soundTimer = 0
	
	var frameBuffer = new FrameBuffer(Main.ScreenWidth, Main.ScreenHeight)

	def this(state: VMState) = {
		this()
		System.arraycopy(state.ram, 0, this.ram, 0, state.ram.length)
		System.arraycopy(state.registers, 0, this.registers, 0, state.registers.length)
		System.arraycopy(state.stack, 0, this.stack, 0, state.stack.length)
		this.frameBuffer = new FrameBuffer(state.frameBuffer)
		this.i = state.i
		this.pc = state.pc
		this.sp = state.sp
		this.delayTimer = state.delayTimer
		this.soundTimer = state.soundTimer
	}

	val DefaultSprites = Array(
		Array(0xF0, 0x90, 0x90, 0x90, 0xF0), Array(0x20, 0x60, 0x20, 0x20, 0x70),
		Array(0xF0, 0x10, 0xF0, 0x80, 0xF0), Array(0xF0, 0x10, 0xF0, 0x10, 0xF0),
		Array(0x90, 0x90, 0xF0, 0x10, 0x10), Array(0xF0, 0x80, 0xF0, 0x10, 0xF0),
		Array(0xF0, 0x80, 0xF0, 0x90, 0xF0), Array(0xF0, 0x10, 0x20, 0x40, 0x40),
		Array(0xF0, 0x90, 0xF0, 0x90, 0xF0), Array(0xF0, 0x90, 0xF0, 0x10, 0xF0),
		Array(0xF0, 0x90, 0xF0, 0x90, 0x90), Array(0xE0, 0x90, 0xE0, 0x90, 0xE0),
		Array(0xF0, 0x80, 0x80, 0x80, 0xF0), Array(0xE0, 0x90, 0x90, 0x90, 0xE0),
		Array(0xF0, 0x80, 0xF0, 0x80, 0xF0), Array(0xF0, 0x80, 0xF0, 0x80, 0x80))

	val SpriteStartAddress = 0

	private var tmp = SpriteStartAddress

	for (j <- 0 until DefaultSprites.length) {
		for (k <- 0 until DefaultSprites(j).length) {
			ram(tmp) = DefaultSprites(j)(k).toByte
			tmp += 1
		}
	}

}