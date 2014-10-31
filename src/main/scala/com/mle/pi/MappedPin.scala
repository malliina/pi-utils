package com.mle.pi

import com.pi4j.io.gpio.Pin

/**
 * @author Michael
 */
class MappedPin(val pin: Pin, val boardNumber: Int)

object MappedPin {
  def apply(pin: Pin, number: Int) = new MappedPin(pin, number)
}