package de.sebastiankreutzer.chip8

class Dissasembler extends Decoder {

  var sourceCode = ""

  def dissasemble(rom: Rom): String = {
    val code = rom.getByteArray
    for (i <- 0 until code.length by 2) {
      sourceCode += i + ": "
      decode(i, code(i), code(i + 1))
      sourceCode += "\n"
    }
    sourceCode
  }

  override def before(opCode: Int, addr: Int) = {
  }

  override def unknown(opCode: Int, addr: Int) = {
    sourceCode += "Unknown op code: " + opCode.toHexString
  }

  override def clear() = {
    sourceCode += "clear"
  }

  override def ret() = {
    sourceCode += "return"
  }

  override def jmp(addr: Int) {
    sourceCode += "jmp " + addr
  }

  override def call(addr: Int) {
    sourceCode += "call " + addr
  }

  override def jeq(reg: Int, value: Int) {
    sourceCode += "jeq " + reg + " " + value
  }

  override def jneq(reg: Int, value: Int) {
    sourceCode += "jneq " + reg + " " + value
  }

  override def jeqr(reg1: Int, reg2: Int) {
    sourceCode += "jeqr " + reg1 + " " + reg2
  }

  override def set(reg: Int, value: Int) {
    sourceCode += "set " + reg + " " + value
  }

  override def add(reg: Int, value: Int) {
    sourceCode += "add " + reg + " " + value
  }

  override def setr(reg1: Int, reg2: Int) {
    sourceCode += "setr " + reg1 + " " + reg2
  }

  override def or(reg1: Int, reg2: Int) {
    sourceCode += "or " + reg1 + " " + reg2
  }

  override def and(reg1: Int, reg2: Int) {
    sourceCode += "and " + reg1 + " " + reg2
  }

  override def xor(reg1: Int, reg2: Int) {
    sourceCode += "xor " + reg1 + " " + reg2
  }

  override def addr(reg1: Int, reg2: Int) {
    sourceCode += "addr " + reg1 + " " + reg2
  }

  override def sub(reg1: Int, reg2: Int) {
    sourceCode += "sub " + reg1 + " " + reg2
  }

  override def shr(reg1: Int, reg2: Int) {
    sourceCode += "shr " + reg1 + " " + reg2
  }

  override def subb(reg1: Int, reg2: Int) {
    sourceCode += "subb " + reg1 + " " + reg2
  }

  override def shl(reg1: Int, reg2: Int) {
    sourceCode += "shl " + reg1 + " " + reg2
  }

  override def jneqr(reg1: Int, reg2: Int) {
    sourceCode += "jneqr " + reg1 + " " + reg2
  }

  override def seti(value: Int) {
    sourceCode += "seti " + value
  }

  override def jmpv0(addr: Int) {
    sourceCode += "jmpv0 " + addr
  }

  override def rand(reg: Int, value: Int) {
    sourceCode += "ramd " + reg + " " + value
  }

  override def draw(reg1: Int, reg2: Int, value: Int) {
    sourceCode += "draw " + reg1 + " " + reg2 + " " + value
  }

  override def jkey(reg: Int) {
    sourceCode += "jkey " + reg
  }

  override def jnkey(reg: Int) {
    sourceCode += "jnkey " + reg
  }

  override def getdelay(reg: Int) {
    sourceCode += "getdelay " + reg
  }

  override def waitkey(reg: Int) {
    sourceCode += "waitkey " + reg
  }

  override def setdelay(reg: Int) {
    sourceCode += "setdelay " + reg
  }

  override def setsound(reg: Int) {
    sourceCode += "setsound " + reg
  }

  override def addi(reg: Int) {
    sourceCode += "addi " + reg
  }

  override def spritei(reg: Int) {
    sourceCode += "spritei " + reg
  }

  override def bcd(reg: Int) {
    sourceCode += "bcd " + reg
  }

  override def push(reg: Int) {
    sourceCode += "push " + reg
  }

  override def pop(reg: Int) {
    sourceCode += "pop " + reg
  }

}