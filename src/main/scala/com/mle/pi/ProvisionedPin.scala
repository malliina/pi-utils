package com.mle.pi

import com.mle.concurrent.Futures
import com.pi4j.io.gpio.{GpioPinDigitalOutput, PinState}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

/**
 * @author Michael
 */
case class ProvisionedPin(outPin: GpioPinDigitalOutput, pin: StatefulPin) {
  val enableState = pin.enableState
  val disableState = pin.disableState
  val number = pin.number

  def enable() = setState(enableState)

  def disable() = setState(disableState)

  def enableTimed(duration: Duration): Future[Unit] = setTimed(duration, enableState)

  def setTimed(duration: Duration, state: PinState): Future[Unit] = {
    setState(state)
    println(s"Pin: $outPin set to state: $state")
    val revertState = PinState getInverseState state
    Futures.after(duration) {
      setState(revertState)
      println(s"Pin: $outPin set to state: $revertState")
    }
  }

  def setState(state: PinState) = outPin setState state
}

