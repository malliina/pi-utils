package com.mle.pi

import com.pi4j.io.gpio.PinState

/**
 * @author Michael
 */
case class DigitalPin(plan: PinPlan, enableState: PinState) extends MappedPin(plan.pin, plan.boardNumber) {
  val disableState = PinState.getInverseState(enableState)
}