package de.sebastiankreutzer.chip8

import scala.util.Random

class Chip8VM(surface: Surface) extends Decoder {

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
  
  val ZeroSprite = 0
  
  ram(ZeroSprite) = 0xF
  ram(ZeroSprite+ 1) = 0x9
  ram(ZeroSprite + 2) = 0x9
  ram(ZeroSprite + 3) = 0x9
  ram(ZeroSprite + 4) = 0xF

  
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
        soundTimer = math.max(0, soundTimer - 1)
        if (delayTimer == 0) {
          println("delay is 0")
        }
        last = time
      }
//      println("Next instruction at " + pc.toHexString + ": " + load(pc).toHexString  +  load(pc + 1).toHexString)
      decode(pc, load(pc), load(pc + 1))
      pc += 2
    }
  }

  def getRegister(register: Int): Int = {
    if (register < 0 || register >= registers.length) {
      error(register + " is not a valid register")
      0
    }
    registers(register).toInt
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
    if (getRegister(reg) == value)
      pc += 2
  }

  override def jneq(reg: Int, value: Int) {
    if (getRegister(reg) != value)
      pc += 2
  }

  override def jeqr(reg1: Int, reg2: Int) {
    if (getRegister(reg1) == getRegister(reg2))
      pc += 2
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
    setRegister(reg1, getRegister(reg1) + getRegister(reg2))
  }

  override def sub(reg1: Int, reg2: Int) {
    setRegister(reg1, getRegister(reg1) - getRegister(reg2))
  }

  override def shr(reg1: Int, reg2: Int) {
    setRegister(reg1, getRegister(reg2) >> 1)
  }

  override def subb(reg1: Int, reg2: Int) {
    setRegister(reg1, getRegister(reg2) - getRegister(reg1))
  }

  override def shl(reg1: Int, reg2: Int) {
    setRegister(reg1, getRegister(reg2) << 1)
  }

  override def jneqr(reg1: Int, reg2: Int) {
    if (getRegister(reg1) != getRegister(reg2))
      pc += 2
  }

  override def seti(value: Int) {
    i = value
  }

  override def jmpv0(addr: Int) {
    pc = addr + getRegister(0) - 2
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
      sprite(j) = load(i + j)
      output.append(sprite(j).toHexString)
      output += ' '
    }
    surface.drawSprite(x, y, sprite)
//    println("draw sprite: " + output)
  }

  override def jkey(reg: Int) {

  }

  override def jnkey(reg: Int) {

  }

  override def getdelay(reg: Int) {
    setRegister(reg, delayTimer)
  }

  override def waitkey(reg: Int) {

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
    i = x match {
      case 0 => ZeroSprite
      case _ => ZeroSprite
    }
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

}