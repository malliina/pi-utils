package com.mle.pi

import java.io.Closeable

import com.mle.pi.PinEvents.PinChangedEvent
import com.mle.rx.Observables
import com.pi4j.io.gpio.{GpioFactory, GpioPin}
import rx.lang.scala.Observable

/**
 * @author Michael
 */
class PinController(digitals: Seq[DigitalPin], pwms: Seq[PwmPin]) extends Closeable {
  protected val gpio = GpioFactory.getInstance()
  val ppins = digitals map provisionDigital
  val ppwms = pwms map provisionPwm
  val digitalEvents = Observables.mergeAll(ppins.map(_.events))
  val pwmEvents = Observables.mergeAll(ppwms.map(_.events))
  val events: Observable[PinChangedEvent] = digitalEvents merge pwmEvents

  def disableAll() = ppins.foreach(_.disable())

  def provisionDigital(pin: DigitalPin) = {
    val provisioned = gpio.provisionDigitalOutputPin(pin.pin, pin.disableState)
    provisioned.setShutdownOptions(true, pin.disableState)
    ProvisionedDigitalPin(provisioned, pin)
  }

  def provisionPwm(pin: PwmPin) = {
    val provisioned = gpio.provisionPwmOutputPin(pin.pin)
    provisioned.setShutdownOptions(true)
    ProvisionedPwmPin(provisioned, pin)
  }

  def unprovision(pins: Seq[ProvisionedPin[_ <: GpioPin, _]]) = {
    gpio.unprovisionPin(pins.map(_.pin): _*)
    pins.foreach(_.subject.onCompleted())
  }

  override def close(): Unit = {
    gpio.shutdown()
    unprovision(ppins ++ ppwms)
  }
}

class PinControl(pins: Seq[PinPlan]) extends Closeable {
  protected val gpio = GpioFactory.getInstance()

  override def close(): Unit = {
    gpio.shutdown()
  }
}