package com.mle.pi

import java.io.Closeable

import com.pi4j.io.gpio.{GpioPin, GpioFactory, Pin}

/**
 * @author Michael
 */
class PinController(pins: Seq[DigitalPin], pwms: Seq[PwmPin]) extends Closeable {
  private val gpio = GpioFactory.getInstance()
  val ppins = provisionAll(pins)
  val ppwms = pwms map provisionPwm
  //  val events = Observable[GpioPinDigitalStateChangeEvent](subscriber => {
  //    val listener = new GpioPinListenerDigital {
  //      override def handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent): Unit =
  //        subscriber onNext event
  //    }
  //    gpio.add
  //    gpio.addListener(listener,ppins.map(_.outPin):_*)
  //    subscriber add Subscription(gpio removeListener listener)
  //  })

  //  val listener = new GpioPinListenerAnalog {
  //    override def handleGpioPinAnalogValueChangeEvent(event: GpioPinAnalogValueChangeEvent): Unit = event.
  //  }

  def get(pin: Pin): Option[ProvisionedDigitalPin] = ppins.find(_.backing.pin == pin)

  def disableAll() = ppins.foreach(_.disable())

  private def provisionAll(leds: Seq[DigitalPin]): Seq[ProvisionedDigitalPin] = leds map provision

  private def provision(pin: DigitalPin) = {
    val provisioned = gpio.provisionDigitalOutputPin(pin.pin, pin.disableState)
    provisioned.setShutdownOptions(true, pin.disableState)
    ProvisionedDigitalPin(provisioned, pin)
  }

  private def provisionPwm(pin: MappedPin2) = {
    val provisioned = gpio.provisionPwmOutputPin(pin.pin)
    provisioned.setShutdownOptions(true)
    ProvisionedPwmPin(provisioned, pin.number)
  }

  private def unprovision(pins: Seq[ProvisionedPin[_ <: GpioPin]]) = gpio.unprovisionPin(pins.map(_.pin): _*)

  override def close(): Unit = {
    gpio.shutdown()
    unprovision(ppins ++ ppwms)
  }
}
