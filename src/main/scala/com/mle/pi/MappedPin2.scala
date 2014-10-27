package com.mle.pi

import com.pi4j.io.gpio.Pin

/**
 * @author Michael
 */
class MappedPin2(val pin: Pin, val number: Int)

object MappedPin2 {
  def apply(pin: Pin, number: Int) = new MappedPin2(pin, number)
}