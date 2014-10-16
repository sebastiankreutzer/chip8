package de.sebastiankreutzer.chip8

import java.awt.Graphics
import javax.swing.JPanel
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

class Screen extends JPanel(true) with Surface {

  val ScreenWidth = 64
  val ScreenHeight = 32

  val Black = 0x000000
  val White = 0xFFFFFF

  val img = new BufferedImage(ScreenWidth, ScreenHeight, BufferedImage.TYPE_INT_RGB)
  val data = img.getRaster().getDataBuffer().asInstanceOf[DataBufferInt].getData()

  override def drawSprite(x: Int, y: Int, sprite: Array[Byte]) {
//    println("*********")
    for (i <- 0 until sprite.length) {
      for (j <- 0 until 8) {
        if ((sprite(i) & (1 << j)) >> j == 1) {        
          togglePixel(x + j, y + i)
//          printf("x")
        }else {
//          printf(" ")
        }
      }
//      println()
    }
//    println("*********")
    repaint();
  }

  def togglePixel(x: Int, y: Int) {
    if (x >= 0 && y >= 0 && x < ScreenWidth && y < ScreenHeight) {
      if (data(y * ScreenWidth + x) == White)
        data(y * ScreenWidth + x) = Black
      else
        data(y * ScreenWidth + x) = White
    }
  }

  override def clear() {
    for (i <- 0 to data.length) data(i) = Black
    repaint()
  }

  override def paint(g: Graphics) {
    g.drawImage(img, 0, 0, getWidth, getHeight, this)
  }
  
  override def repaint() {
    super.repaint()
//    println("Now printing screen:")
//    println("********************")
//    for (i <- 0 until ScreenHeight) {
//      for(j <- 0 until ScreenWidth) {
//        printf(if (data(i * ScreenWidth + j) == Black) "x" else " ")
//      }
//      println()
//    }
//    println("********************")
  }

}