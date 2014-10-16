package de.sebastiankreutzer.chip8

import javax.swing.JPanel
import javax.swing.JFrame
import java.awt.Dimension

class UI extends JFrame {

  
  val Size = new Dimension(800, 600)
  val screen = new Screen()

  setTitle("Scala8")
  setVisible(true)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  screen.setPreferredSize(Size)
  screen.setMinimumSize(Size)
  screen.setMaximumSize(Size)
  setContentPane(screen)
  pack()
  setLocationRelativeTo(null)

  def getScreen(): Screen = screen

}