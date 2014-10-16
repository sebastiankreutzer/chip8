package de.sebastiankreutzer.chip8

import scala.io.Codec
import scala.io.Source
import java.nio.charset.CodingErrorAction
import java.nio.file.Paths
import java.nio.file.Files

class Rom(file: String) {

//  implicit val codec = Codec("UTF-8")
//  codec.onMalformedInput(CodingErrorAction.REPLACE)
//  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
//  val source = Source.fromFile(file)
//  val code = source.map(_.toByte).toArray
//  source.close()
  
  val code = Files.readAllBytes(Paths.get(file))

  def getByteArray() : Array[Byte] = code

}