package com.mle.pi

import java.io.Closeable

import com.mle.pi.PinEvents.PinChangedEvent
import com.pi4j.io.gpio.{GpioFactory, GpioPin, Pin, PinMode}
import rx.lang.scala.Observable

/**
 * @author Michael
 */
class PinController(digitals: Seq[DigitalPin], pwms: Seq[PwmPin]) extends Closeable {
  private val gpio = GpioFactory.getInstance()
  val ppins = digitals map provisionDigital
  val ppwms = pwms map provisionPwm
  val digitalEvents = mergeAll(ppins.map(_.events))
  val pwmEvents = mergeAll(ppwms.map(_.events))
  val events: Observable[PinChangedEvent] = digitalEvents merge pwmEvents

  def mergeAll[T](events: Seq[Observable[T]]) =
    events.foldLeft[Observable[T]](Observable.empty)((acc, elem) => acc merge elem)

  def get(pin: Pin): Option[ProvisionedDigitalPin] = ppins.find(_.backing.pin == pin)

  def disableAll() = ppins.foreach(_.disable())

  private def provisionDigital(pin: DigitalPin) = {
    val provisioned = gpio.provisionDigitalOutputPin(pin.pin, pin.disableState)
    provisioned.setShutdownOptions(true, pin.disableState)
    ProvisionedDigitalPin(provisioned, pin)
  }

  private def provisionPwm(pin: PwmPin) = {
    val provisioned = gpio.provisionPwmOutputPin(pin.pin)
    provisioned.setShutdownOptions(true)
    ProvisionedPwmPin(provisioned, pin.number)
  }

  private def unprovision(pins: Seq[ProvisionedPin[_ <: GpioPin, _]]) = {
    gpio.unprovisionPin(pins.map(_.pin): _*)
    pins.foreach(_.subject.onCompleted())
  }

  override def close(): Unit = {
    gpio.shutdown()
    unprovision(ppins ++ ppwms)
  }
}
