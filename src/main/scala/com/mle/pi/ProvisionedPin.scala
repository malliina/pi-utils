package com.mle.pi

import com.mle.pi.PinEvents.PinChangedEvent
import com.pi4j.io.gpio.GpioPin
import rx.lang.scala.{Observable, Subject}

/**
 * @author Michael
 */
trait ProvisionedPin[T <: GpioPin, E <: PinChangedEvent] {
  val subject = Subject[E]()
  val events: Observable[E] = subject

  val pin: T
  val number: Int
}