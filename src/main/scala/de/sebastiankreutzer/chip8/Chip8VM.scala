package de.sebastiankreutzer.chip8

import java.awt.Toolkit

import scala.util.Random

class Chip8VM(surface: Surface, inputProcessor: InputProcessor) extends Decoder {

	val DefaultFrequency = 60.0f

	var frequency = DefaultFrequency

	val TimerFrequency = 60.0f

	var state: VMState = new VMState

	val random = new Random()

	val RunContinuosly = 0
	val RunStep = 1

	var mode = RunContinuosly
	var stepsToRun = 0
	var running = false

	var paused = false

	var thread: Option[VMThread] = None

	class VMThread extends Thread {

		override def run() {

			println("VM thread is running")

			var time = System.nanoTime();
			var last = time

			var unprocessedTime = 0.0f

			surface.updateScreen(state.frameBuffer)

			while (running) {

				while (!paused && (stepsToRun > 0 || mode == RunContinuosly)) {

					time = System.nanoTime()

					if (time - last >= 1000000000.0f / frequency) {

						unprocessedTime += (time - last) / TimerFrequency

						if (unprocessedTime >= 1) {
							state.delayTimer = math.max(0, state.delayTimer - 1)
							if (state.soundTimer > 0) {
								state.soundTimer -= 1
								if (state.soundTimer == 0)
									surface.playSound()
							}
							unprocessedTime -= 1
						}

						decode(state.pc, load(state.pc), load(state.pc + 1))
						state.pc += 2
						//					println("Next instruction at " + state.pc.toHexString + ": " + load(state.pc).toHexString + load(state.pc + 1).toHexString)

						last = time

						if (mode != RunContinuosly)
							stepsToRun -= 1
					}

				}
			}

			println("VM thread terminated")

		}

	}

	def loadState(state: VMState) {
		this.state = state
		surface.updateScreen(state.frameBuffer)
		if (!running) {
			start()
		}
	}

	def start() {
		if (!running) {
			thread = thread match {
				case None => Some(new VMThread)
				case Some(thread) => Some(thread)
			}
			running = true
			thread.get.start()
		}
	}

	def pause() {
		paused = true
		println("VM paused")
	}

	def resume() {
		paused = false
		println("VM resumed")
	}

	def stop() {
		if (running) {
			running = false
			thread.get.join()
		}
	}

	//	def run() {
	//		println("VM is now running")
	//		println("*****************")
	//		var time = System.nanoTime();
	//		var last = time
	//		val timeout = 1000000000.0f / FPS
	//		while (true) {
	//			time = System.nanoTime()
	//			if (time - last >= timeout) {
	//				state.delayTimer = math.max(0, state.delayTimer - 1)
	//				if (state.soundTimer > 0) {
	//					state.soundTimer -= 1
	//					if (state.soundTimer == 0)
	//						Toolkit.getDefaultToolkit().beep()
	//				}
	//				last = time
	//			}
	//			//println("Next instruction at " + pc.toHexString + ": " + load(pc).toHexString  +  load(pc + 1).toHexString)
	//			decode(state.pc, load(state.pc), load(state.pc + 1))
	//			state.pc += 2
	//			Thread.sleep(2);
	//		}
	//	}

	def getRegisterByte(register: Int): Byte = {
		if (register < 0 || register >= state.registers.length) {
			error(register + " is not a valid register")
			0
		}
		state.registers(register)
	}

	def getRegister(register: Int): Int = {
		getRegisterByte(register).toInt
	}

	def setRegister(register: Int, value: Int) = {
		if (register < 0 || register >= state.registers.length)
			error(register + " is not a valid register")
		else
			state.registers(register) = (value & 0xFF) toByte
	}

	def store(addr: Int, value: Byte) {
		if (addr < 0 || addr >= state.ram.length)
			error(addr + " is not a valid memory address")
		else
			state.ram(addr) = value
	}

	def store(addr: Int, value: Int) {
		store(addr, (value & 0xFF).toByte)
	}

	def load(addr: Int): Byte = {
		if (addr < 0 || addr >= state.ram.length) {
			error(addr + " is not a valid memory address")
			0
		} else state.ram(addr)
	}

	def error(msg: String) = {
		println("VM Error: " + msg);
		stop()
	}

	override def before(opCode: Int, addr: Int) = {

	}

	override def unknown(opCode: Int, addr: Int) = {
		println("Received unknown op code!")
	}

	override def clear() {
		state.frameBuffer.clear()
		surface.updateScreen(state.frameBuffer)
	}

	override def ret() {
		state.sp -= 1
		state.pc = state.stack(state.sp)
	}

	override def jmp(addr: Int) {
		state.pc = addr - 2
	}

	override def call(addr: Int) {
		state.stack(state.sp) = state.pc
		state.sp += 1
		state.pc = addr - 2
	}

	override def jeq(reg: Int, value: Int) {
		if (getRegister(reg) == value) {
			state.pc += 2
		}
	}

	override def jneq(reg: Int, value: Int) {
		if (getRegister(reg) != value) {
			state.pc += 2
		}
	}

	override def jeqr(reg1: Int, reg2: Int) {
		if (getRegister(reg1) == getRegister(reg2))
			state.pc += 2
	}

	override def set(reg: Int, value: Int) {
		setRegister(reg, value)
	}

	override def add(reg: Int, value: Int) {
		setRegister(reg, getRegister(reg) + value)
	}

	override def setr(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg2))
	}

	override def or(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg1) | getRegister(reg2))
	}

	override def and(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg1) & getRegister(reg2))
	}

	override def xor(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg1) ^ getRegister(reg2))
	}

	override def addr(reg1: Int, reg2: Int) {

		val v1 = getRegister(reg1)
		val v2 = getRegister(reg2)

		val result = v1 + v2
		setRegister(reg1, result)

		val carry = (v1 & 0xFF) + (v2 & 0xFF) > 0xFF

		setRegister(15, if (carry) 1 else 0)

	}

	override def sub(reg1: Int, reg2: Int) {
		val carry = ((getRegisterByte(reg2) & 0xFF) > (getRegisterByte(reg1) & 0xFF))
		setRegister(reg1, getRegister(reg1) - getRegister(reg2))
		setRegister(15, if (carry) 0 else 1)
	}

	override def shr(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg2) >> 1)
	}

	override def subb(reg1: Int, reg2: Int) {
		val carry = ((getRegisterByte(reg1) & 0xFF) > (getRegisterByte(reg2) & 0xFF))
		setRegister(reg1, getRegister(reg2) - getRegister(reg1))
		setRegister(15, if (carry) 0 else 1)
	}

	override def shl(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg2) << 1)
	}

	override def jneqr(reg1: Int, reg2: Int) {
		if (getRegister(reg1) != getRegister(reg2)) {
			state.pc += 2
		}
	}

	override def seti(value: Int) {
		state.i = value
	}

	override def jmpv0(addr: Int) {
		state.pc = addr + getRegister(0) - 2
	}

	override def rand(reg: Int, value: Int) {
		setRegister(reg, random.nextInt() & value)
	}

	override def draw(reg1: Int, reg2: Int, value: Int) {
		val x = getRegister(reg1)
		val y = getRegister(reg2)
		val height = value
		val sprite = new Array[Byte](height)
		val output: StringBuilder = new StringBuilder()
		for (j <- 0 until height) {
			sprite(j) = load(state.i + j)
			output.append(sprite(j).toHexString)
			output += ' '
		}
		val pixelsUnset = state.frameBuffer.drawSprite(x, y, sprite)
		setRegister(15, if (pixelsUnset) 1 else 0)
		surface.updateScreen(state.frameBuffer)
	}

	override def jkey(reg: Int) {
		val key = getRegister(reg)
		if (inputProcessor.isKeyDown(key))
			state.pc += 2
	}

	override def jnkey(reg: Int) {
		val key = getRegister(reg)
		if (!inputProcessor.isKeyDown(key))
			state.pc += 2
	}

	override def getdelay(reg: Int) {
		setRegister(reg, state.delayTimer)
	}

	override def waitkey(reg: Int) {
		println("Waiting for key")
		var key = inputProcessor.getPressedKey()
		if (key != -1) {
			setRegister(reg, key)
		} else {
			state.pc -= 2
		}
	}

	override def setdelay(reg: Int) {
		state.delayTimer = getRegister(reg)
	}

	override def setsound(reg: Int) {
		state.soundTimer = getRegister(reg)
	}

	override def addi(reg: Int) {
		state.i += getRegister(reg)
	}

	override def spritei(reg: Int) {
		val x = getRegister(reg) & 0xF
		state.i = state.SpriteStartAddress + 5 * x
	}

	override def bcd(reg: Int) {
		var n = getRegister(reg)
		var bcd = Array(0, 0, 0)
		for (j <- 0 to 7) {
			bcd = bcd.map(col => if (col >= 5) col + 3 else col)
			bcd(0) = bcd(0) << 1
			bcd(0) = (bcd(0) & 0xE) | ((bcd(1) & 0x8) >> 3)
			bcd(1) = bcd(1) << 1
			bcd(1) = (bcd(1) & 0xE) | ((bcd(2) & 0x8) >> 3)
			bcd(2) = bcd(2) << 1
			bcd(2) = (bcd(2) & 0xE) | ((n & 0x80) >> 7)
			n = n << 1
		}
		store(state.i, bcd(0))
		store(state.i + 1, bcd(1))
		store(state.i + 2, bcd(2))
		//println("bcd of " + n + " is " + bcd(0) + ", " + bcd(1) + ", " + bcd(2))
	}

	override def push(reg: Int) {
		for (j <- 0 to reg) {
			store(state.i + j, getRegister(j))
		}
		state.i += reg + 1
	}

	override def pop(reg: Int) {
		for (j <- 0 to reg) {
			setRegister(j, load(state.i + j))
		}
		state.i += reg + 1
	}

}