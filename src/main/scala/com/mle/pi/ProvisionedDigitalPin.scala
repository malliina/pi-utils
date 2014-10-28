package com.mle.pi

import com.mle.concurrent.Futures
import com.mle.pi.PinEvents.DigitalStateChanged
import com.mle.util.Log
import com.pi4j.io.gpio.{GpioPinDigitalOutput, PinState}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

/**
 * @author Michael
 */
case class ProvisionedDigitalPin(pin: GpioPinDigitalOutput, backing: DigitalPin)
  extends ProvisionedPin[GpioPinDigitalOutput, DigitalStateChanged] with Log {
  val enableState = backing.enableState
  val disableState = backing.disableState
  val number = backing.number

  def enable() = setState(enableState)

  def disable() = setState(disableState)

  def enableTimed(duration: Duration): Future[Unit] = setTimed(duration, enableState)

  def setTimed(duration: Duration, state: PinState): Future[Unit] = {
    setState(state)
    val revertState = PinState getInverseState state
    Futures.after(duration) {
      setState(revertState)
    }
  }

  def setState(state: PinState) = {
    pin setState state
    subject onNext DigitalStateChanged(this, state)
    log info s"Pin: $pin set to state: $state"
  }
}

