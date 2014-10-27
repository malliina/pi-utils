package com.mle.pi

import com.pi4j.io.gpio.GpioPin

/**
 * @author Michael
 */
trait ProvisionedPin[T <: GpioPin] {
  val pin: T
  val number: Int
}