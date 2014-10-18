package de.sebastiankreutzer.chip8

import java.awt.Toolkit

import scala.util.Random

class Chip8VM(surface: Surface, inputProcessor: InputProcessor) extends Decoder {

	val FPS = 60.0f

	val ram = new Array[Byte](4096)

	var pc = 0x200

	val registers = new Array[Byte](16)

	var i = 0

	val stack = new Array[Int](16)
	var sp = 0

	var delayTimer = 0
	var soundTimer = 0

	val random = new Random()

	val DefaultSprites = Array(
		Array(0xF, 0x9, 0x9, 0x9, 0xF), Array(0x2, 0x6, 0x2, 0x2, 0x7),
		Array(0xF, 0x1, 0xF, 0x8, 0xF), Array(0xF, 0x1, 0xF, 0x1, 0xF),
		Array(0x9, 0x9, 0xF, 0x1, 0x1), Array(0xF, 0x8, 0xF, 0x1, 0xF),
		Array(0xF, 0x8, 0xF, 0x9, 0xF), Array(0xF, 0x1, 0x2, 0x4, 0x4),
		Array(0xF, 0x9, 0xF, 0x9, 0xF), Array(0xF, 0x9, 0xF, 0x1, 0xF),
		Array(0xF, 0x9, 0xF, 0x9, 0x9), Array(0xE, 0x9, 0xE, 0x9, 0xE),
		Array(0xF, 0x8, 0x8, 0x8, 0xF), Array(0xE, 0x9, 0x9, 0x9, 0xE),
		Array(0xF, 0x8, 0xF, 0x8, 0xF), Array(0xF, 0x8, 0xF, 0x8, 0x8))

	val SpriteStartAddress = 0

	private var tmp = SpriteStartAddress

	for (j <- 0 until DefaultSprites.length) {
		for (k <- 0 until DefaultSprites(j).length) {
			ram(tmp) = DefaultSprites(j)(k).toByte
			tmp += 1
		}
	}

	def loadRom(rom: Rom) {
		val code = rom.getByteArray
		System.arraycopy(code, 0, ram, 0x200, code.length)
	}

	def run() {
		println("VM is now running")
		println("*****************")
		var time = System.nanoTime();
		var last = time
		val timeout = 1000000000.0f / FPS
		while (true) {
			time = System.nanoTime()
			if (time - last >= timeout) {
				delayTimer = math.max(0, delayTimer - 1)
				if (soundTimer > 0) {
					soundTimer -= 1
					if (soundTimer == 0)
						Toolkit.getDefaultToolkit().beep()
				}
				last = time
			}
			//println("Next instruction at " + pc.toHexString + ": " + load(pc).toHexString  +  load(pc + 1).toHexString)
			decode(pc, load(pc), load(pc + 1))
			pc += 2
			Thread.sleep(2);
		}
	}

	def getRegisterByte(register: Int): Byte = {
		if (register < 0 || register >= registers.length) {
			error(register + " is not a valid register")
			0
		}
		registers(register)
	}

	def getRegister(register: Int): Int = {
		getRegisterByte(register).toInt
	}

	def setRegister(register: Int, value: Int) = {
		if (register < 0 || register >= registers.length)
			error(register + " is not a valid register")
		else
			registers(register) = (value & 0xFF) toByte
	}

	def store(addr: Int, value: Byte) {
		if (addr < 0 || addr >= ram.length)
			error(addr + " is not a valid memory address")
		else
			ram(addr) = value
	}

	def store(addr: Int, value: Int) {
		store(addr, (value & 0xFF).toByte)
	}

	def load(addr: Int): Byte = {
		if (addr < 0 || addr >= ram.length) {
			error(addr + " is not a valid memory address")
			0
		} else ram(addr)
	}

	def error(msg: String) = {
		println("VM Error: " + msg);
	}

	override def before(opCode: Int, addr: Int) = {

	}

	override def unknown(opCode: Int, addr: Int) = {
		println("Received unknown op code!")
	}

	override def clear() {
		surface.clear()
	}

	override def ret() {
		sp -= 1
		pc = stack(sp)
		println("Returned from subroutine")
	}

	override def jmp(addr: Int) {
		pc = addr - 2
	}

	override def call(addr: Int) {
		stack(sp) = pc
		sp += 1
		pc = addr - 2
		println("Calling subroutine " + pc)
	}

	override def jeq(reg: Int, value: Int) {
		if (getRegister(reg) == value) {
			println(getRegister(reg) + " = " + value + " -> skip")
			pc += 2
		} else {
			println(getRegister(reg) + " != " + value + " -> no skip")
		}
	}

	override def jneq(reg: Int, value: Int) {
		if (getRegister(reg) != value) {
			pc += 2
			println("unequal -> skip")
		} else {
			println("equal -> no skip")
		}
	}

	override def jeqr(reg1: Int, reg2: Int) {
		if (getRegister(reg1) == getRegister(reg2))
			pc += 2
	}

	override def set(reg: Int, value: Int) {
		setRegister(reg, value)
		println("set V" + reg + " to " + getRegister(reg))
	}

	override def add(reg: Int, value: Int) {
		print(getRegister(reg) + " + " + value + " = ")
		setRegister(reg, getRegister(reg) + value)
		println(getRegister(reg) + " to V" + reg)
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
		print(getRegister(reg1) + " + " + getRegister(reg2) + " = ")
		val result = getRegister(reg1) + getRegister(reg2)
		setRegister(reg1, result)

		val carry = checkOverflowOnAdd(getRegisterByte(reg1), getRegisterByte(reg2))

		setRegister(15, if (carry) 1 else 0)

		println(getRegister(reg1) + " to V" + reg1)

		if (carry)
			println("carry!")
	}

	override def sub(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg1) - getRegister(reg2))
		val carry = ((getRegisterByte(reg2) & 0xFF) > (getRegisterByte(reg1) & 0xFF))
		if (carry)
			println("carry!")
		setRegister(15, if (carry) 1 else 0)
	}

	override def shr(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg2) >> 1)
	}

	override def subb(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg2) - getRegister(reg1))
		val carry = ((getRegisterByte(reg1) & 0xFF) > (getRegisterByte(reg2) & 0xFF))
		if (carry)
			println("carry!")
		setRegister(15, if (carry) 1 else 0)
	}

	override def shl(reg1: Int, reg2: Int) {
		setRegister(reg1, getRegister(reg2) << 1)
	}

	override def jneqr(reg1: Int, reg2: Int) {
		if (getRegister(reg1) != getRegister(reg2)) {
			pc += 2
			println("unequal -> skip")
		} else {
			println("equal -> no skip")
		}
	}

	override def seti(value: Int) {
		i = value
	}

	override def jmpv0(addr: Int) {
		pc = addr + getRegister(0) - 2
	}

	override def rand(reg: Int, value: Int) {
		setRegister(reg, random.nextInt() & value)
		println("rand: " + (random.nextInt() & value))
	}

	override def draw(reg1: Int, reg2: Int, value: Int) {
		val x = getRegister(reg1)
		val y = getRegister(reg2)
		val height = value
		val sprite = new Array[Byte](height)
		val output: StringBuilder = new StringBuilder()
		for (j <- 0 until height) {
			sprite(j) = load(i + j)
			output.append(sprite(j).toHexString)
			output += ' '
		}
		val pixelsUnset = surface.drawSprite(x, y, sprite)
		if (pixelsUnset) {
			setRegister(15, 1)
		} else {
			setRegister(15, 0)
		}
		println("draw h=" + height + ", x=" + x + ", y=" + y + ", pixelsUnset=" + pixelsUnset)
	}

	override def jkey(reg: Int) {
		val key = getRegister(reg)
		if (inputProcessor.isKeyDown(key))
			pc += 2
	}

	override def jnkey(reg: Int) {
		val key = getRegister(reg)
		if (!inputProcessor.isKeyDown(key))
			pc += 2
	}

	override def getdelay(reg: Int) {
		setRegister(reg, delayTimer)
	}

	override def waitkey(reg: Int) {
		println("Waiting for key")
		val key = getRegister(reg)
		while (!inputProcessor.isKeyDown(key)) {}
	}

	override def setdelay(reg: Int) {
		delayTimer = getRegister(reg)
	}

	override def setsound(reg: Int) {
		soundTimer = getRegister(reg)
	}

	override def addi(reg: Int) {
		i += getRegister(reg)
	}

	override def spritei(reg: Int) {
		val x = getRegister(reg) & 0xF
		i = SpriteStartAddress + 5 * x
		println("draw " + x)
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
		store(i, bcd(0))
		store(i + 1, bcd(1))
		store(i + 2, bcd(2))
	}

	override def push(reg: Int) {
		for (j <- 0 to reg) {
			store(i + j, getRegister(j))
		}
		i += reg + 1
	}

	override def pop(reg: Int) {
		for (j <- 0 to reg) {
			setRegister(j, load(i + j))
		}
		i += reg + 1
	}

	def checkOverflowOnAdd(a: Byte, b: Byte): Boolean = {
		if (a > b) checkOverflowOnAdd(b, a)
		else {
			if (a < 0) {
				if (b < 0) {
					if (Byte.MinValue - b <= a) false
					else true
				}
				false
			} else {
				if (a <= Byte.MaxValue - b) false
				else true
			}

		}
	}

}