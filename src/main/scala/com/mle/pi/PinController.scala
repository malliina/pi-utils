package com.mle.pi

import java.io.Closeable

import com.pi4j.io.gpio.event.{GpioPinDigitalStateChangeEvent, GpioPinListenerDigital}
import com.pi4j.io.gpio.{PinMode, GpioFactory, Pin}
import rx.lang.scala.{Observable, Subscription}

/**
 * @author Michael
 */
class PinController(pins: Seq[StatefulPin]) extends Closeable {
  private val gpio = GpioFactory.getInstance()
  val ppins = provisionAll(pins)
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

  def get(pin: Pin): Option[ProvisionedPin] = ppins.find(_.pin.pin == pin)

  def disableAll() = ppins.foreach(_.disable())

  private def provisionAll(leds: Seq[StatefulPin]): Seq[ProvisionedPin] = leds map provision

  private def provision(pin: StatefulPin) = {
    val provisioned = gpio.provisionDigitalOutputPin(pin.pin, pin.disableState)
    provisioned.setShutdownOptions(true, pin.disableState)
    ProvisionedPin(provisioned, pin)
  }

  private def unprovision(pins: Seq[ProvisionedPin]) = gpio.unprovisionPin(pins.map(_.outPin): _*)

  override def close(): Unit = {
    gpio.shutdown()
    unprovision(ppins)
  }
}
