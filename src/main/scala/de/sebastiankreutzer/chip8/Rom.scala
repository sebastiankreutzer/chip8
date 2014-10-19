package de.sebastiankreutzer.chip8

import java.nio.file.Files
import java.nio.file.Paths

class Rom(path : String) {

//  implicit val codec = Codec("UTF-8")
//  codec.onMalformedInput(CodingErrorAction.REPLACE)
//  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
//  val source = Source.fromFile(file)
//  val code = source.map(_.toByte).toArray
//  source.close()
  
  val code = Files.readAllBytes(Paths.get(path))

  def reset: VMState = {
  	val state = new VMState
  	System.arraycopy(code, 0, state.ram, 0x200, code.length)
  	state
  } 

}