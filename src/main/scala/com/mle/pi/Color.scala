package com.mle.pi

import scala.util.Random

/**
 * @author Michael
 */
case class Color(red: Boolean, green: Boolean, blue: Boolean) {
  val toSeq = Seq(red, green, blue)
}

object Color {
  def fromString(encodedColor: String) = {
    def toBoolean(c: Char) = if (c == '1') true else false
    val booleans = encodedColor map toBoolean
    Color(booleans(0), booleans(1), booleans(2))
  }

  val off = Color.fromString("000")
  val blue = Color.fromString("001")
  val green = Color.fromString("010")
  val cyan = Color.fromString("011")
  val red = Color.fromString("100")
  val magenta = Color.fromString("101")
  val yellow = Color.fromString("110")
  val white = Color.fromString("111")

  val allColors = Seq(blue, green, cyan, red, magenta, yellow, white)

  def randomElement[T](available: Seq[T]) = {
    val index = Random nextInt available.size
    available(index)
  }

  def randomColor = randomElement(allColors)
}