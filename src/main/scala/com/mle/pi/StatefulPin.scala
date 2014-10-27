package com.mle.pi

import com.pi4j.io.gpio.{Pin, PinState}

/**
 * @author Michael
 */
case class StatefulPin(pin: Pin, number: Int, enableState: PinState) {
  val disableState = PinState.getInverseState(enableState)
}

object StatefulPin {
  //  def rgb(pin: Pin) = StatefulPin(pin, PinState.HIGH)
  //
  //  def led(pin: Pin) = StatefulPin(pin, PinState.LOW)
}