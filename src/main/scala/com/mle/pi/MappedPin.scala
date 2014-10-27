package com.mle.pi

import com.pi4j.io.gpio.{PinState, Pin}

/**
 * @author Michael
 */
case class MappedPin(pin: Pin, number: Int) {
  def high = StatefulPin(pin, number, PinState.HIGH)

  def low = StatefulPin(pin, number, PinState.LOW)
}