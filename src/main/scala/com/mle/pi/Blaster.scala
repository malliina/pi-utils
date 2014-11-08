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
  val subject = Subject[PinChangedEvent]()
  val events: Observable[PinChangedEvent] = subject

  def write(pin: PinPlan, value: PwmValue): Try[Unit] = write(pin.gpioNumber, value)

  /**
   * @param pin
   * @param value [0, 1000]
   * @return
   */
  def write(pin: Int, value: PwmValue): Try[Unit] = {
    // pi-blaster expects a PWM float value in the range [0.0f, 1.0f]
    val scaled = 1.0f * value.value / PwmValue.Max
    write(s"$pin=$scaled").map(_ => subject onNext Pwm(pin, value))
  }

  private def write(line: String): Try[Unit] = Try(FileUtilities.writerTo(file)(_.println(line)))

  def release(pin: PinPlan): Try[Unit] = release(pin.gpioNumber)

  def release(pin: Int): Try[Unit] = {
    // sets PWM to 0 before releasing
    for {
      _ <- write(pin, PwmValue.empty)
      r <- write(s"release $pin")
    } yield {
      subject onNext Released(pin)
      r
    }
  }
}