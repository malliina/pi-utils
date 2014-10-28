package com.mle.pi

import com.pi4j.io.gpio.{Pin, PinState}

/**
 * @author Michael
 */
case class DigitalPin(p: Pin, num: Int, enableState: PinState) extends MappedPin(p, num) {
  val disableState = PinState.getInverseState(enableState)
}