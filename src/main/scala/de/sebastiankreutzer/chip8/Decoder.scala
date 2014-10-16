package de.sebastiankreutzer.chip8

import de.sebastiankreutzer.chip8.Utils.ExtendedByte
import de.sebastiankreutzer.chip8.Utils.ExtendedInt

trait Decoder {
  
  def decode(address: Int, msb: Byte, lsb: Byte) = {
    val opCode = (msb.toInt << 8 | (lsb.toInt & 0xff)) & 0xffff
    val lsbInt = lsb.toInt & 0xFF
    val msbInt = msb.toInt & 0xFF
    before(opCode, address)
    msb.hi match {
      case 0 => lsbInt match {
        case 0xE0 => clear()
        case 0xEE => ret()
        case _ => call(opCode.address)
      }
      case 1 => jmp(opCode.address)
      case 2 => call(opCode.address)
      case 3 => jeq(msb.lo, lsb.toInt)
      case 4 => jeq(msb.lo, lsb.toInt)
      case 5 => jeqr(msb.lo, lsb.hi)
      case 6 => set(msb.lo, lsb.toInt)
      case 7 => add(msb.lo, lsb.toInt)
      case 8 => lsb.lo match {
        case 0 => setr(msb.lo, lsb.hi)
        case 1 => or(msb.lo, lsb.hi)
        case 2 => and(msb.lo, lsb.hi)
        case 3 => xor(msb.lo, lsb.hi)
        case 4 => addr(msb.lo, lsb.hi)
        case 5 => sub(msb.lo, lsb.hi)
        case 6 => shr(msb.lo, lsb.hi)
        case 7 => subb(msb.lo, lsb.hi)
        case 14 => shl(msb.lo, lsb.hi)
        case _ => unknown(opCode, address)
      }
      case 9 => jneqr(msb.lo, lsb.hi)
      case 10 => seti(opCode.address)
      case 11 => jmpv0(opCode.address)
      case 12 => rand(msb.lo, lsb.toInt)
      case 13 => draw(msb.lo, lsb.hi, lsb.lo)
      case 14 => lsbInt match {
        case 0x9E => jkey(msb.lo)
        case 0xA1 => jnkey(msb.lo)
        case _ => unknown(opCode, address)
      }
      case 15 => lsbInt match {
        case 0x07 => getdelay(msb.lo)
        case 0x0A => waitkey(msb.lo)
        case 0x15 => setdelay(msb.lo)
        case 0x18 => setsound(msb.lo)
        case 0x1E => addi(msb.lo)
        case 0x29 => spritei(msb.lo)
        case 0x33 => bcd(msb.lo)
        case 0x55 => push(msb.lo)
        case 0x65 => pop(msb.lo)
        case _ => unknown(opCode, address)
      }
      case _ => unknown(opCode, address)
    }
  }
  
  def before(opCode: Int, addr: Int)
  def unknown(opCode: Int, addr: Int)
  def clear()
  def ret()
  def jmp(addr:Int)
  def call(addr: Int)
  def jeq(reg:Int, value:Int)
  def jneq(reg:Int, value: Int)
  def jeqr(reg1: Int, reg2:Int)
  def set(reg: Int, value: Int)
  def add(reg: Int, value :Int)
  def setr(reg1: Int, reg2:Int)
  def or(reg1: Int, reg2: Int)
  def and(reg1: Int, reg2: Int)
  def xor(reg1: Int, reg2: Int)
  def addr(reg1: Int, reg2: Int)
  def sub(reg1: Int, reg2 : Int)
  def shr(reg1:Int, reg2 : Int)
  def subb(reg1: Int, reg2:Int)
  def shl(reg1: Int, reg2: Int)
  def jneqr(reg1:Int, reg2:Int)
  def seti(value: Int)
  def jmpv0(addr:Int)
  def rand(reg:Int, value:Int)
  def draw(reg1: Int, reg2:Int, value: Int)
  def jkey(reg:Int)
  def jnkey(reg:Int)
  def getdelay(reg:Int)
  def waitkey(reg:Int)
  def setdelay(reg:Int)
  def setsound(reg:Int)
  def addi(reg:Int)
  def spritei(reg:Int)
  def bcd(reg:Int)
  def push(reg:Int)
  def pop(reg:Int)

}