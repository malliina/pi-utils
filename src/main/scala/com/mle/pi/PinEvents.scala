package com.mle.pi

import com.pi4j.io.gpio.PinState

/**
 * @author Michael
 */
object PinEvents {

  trait PinChangedEvent

  case class DigitalStateChanged(pin: ProvisionedDigitalPin, state: PinState) extends PinChangedEvent

  case class PwmChanged(pin: ProvisionedPwmPin, value: Int) extends PinChangedEvent

}