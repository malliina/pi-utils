package com.mle.pi

import com.pi4j.io.gpio.{Pin, PinState}

/**
 * @author Michael
 */
case class PinPlan(pin: Pin, number: Int) {
  def high = DigitalPin(pin, number, PinState.HIGH)

  def low = DigitalPin(pin, number, PinState.LOW)
}