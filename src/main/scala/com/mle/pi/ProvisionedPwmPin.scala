package com.mle.pi

import com.mle.pi.PinEvents.PwmChanged
import com.pi4j.io.gpio.GpioPinPwmOutput

/**
 * @author Michael
 */
case class ProvisionedPwmPin(pin: GpioPinPwmOutput, backing: PwmPin)
  extends ProvisionedPin[GpioPinPwmOutput, PwmChanged] {

  override val boardNumber: Int = backing.boardNumber

  def pwm = pin.getPwm

  def pwm_=(value: Int) = {
    pin setPwm value
    subject onNext PwmChanged(this, value)
  }
}