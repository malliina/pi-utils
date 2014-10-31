package com.mle.pi

import java.nio.file.Paths

import com.mle.file.FileUtilities
import com.mle.pi.PinEvents.{PinChangedEvent, Pwm, Released}
import rx.lang.scala.{Observable, Subject}

import scala.util.Try

/**
 * @author Michael
 */
class Blaster {
  val file = Paths get "/dev/pi-blaster"
  val PWM_MAX = 1000
  val subject = Subject[PinChangedEvent]()
  val events: Observable[PinChangedEvent] = subject

  def write(pin: PinPlan, value: Int): Try[Unit] = write(pin.gpioNumber, value)

  /**
   * @param pin
   * @param value [0, 1000]
   * @return
   */
  def write(pin: Int, value: Int): Try[Unit] = {
    val scaled = 1.0f * value / PWM_MAX
    write(s"$pin=$scaled").map(_ => subject onNext Pwm(pin, value))
  }

  def release(pin: PinPlan): Try[Unit] = release(pin.gpioNumber)

  def release(pin: Int): Try[Unit] = {
    // sets PWM to 0 before releasing
    write(pin, 0).map(_ => write(s"release $pin").map(_ => subject onNext Released(pin)))
  }

  private def write(line: String): Try[Unit] = Try(FileUtilities.writerTo(file)(_.println(line)))
}