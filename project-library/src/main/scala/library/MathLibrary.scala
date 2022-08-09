package library

import domain.Constants

object MathLibrary {
  def add(x: Int, y: Int): Int = x + y
  def circumference(r: Int): Double = 2 * Constants.Pi * r
}
