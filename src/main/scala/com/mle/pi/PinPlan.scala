package com.mle.pi

import com.pi4j.io.gpio.{Pin, PinState}

/**
 * @author Michael
 */
case class PinPlan(pin: Pin, boardNumber: Int, gpioNumber: Int) {
  def high = DigitalPin(this, PinState.HIGH)

  def low = DigitalPin(this, PinState.LOW)

  def pwm = PwmPin(this)
}