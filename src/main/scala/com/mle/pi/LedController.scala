package com.mle.pi

import java.util.concurrent.Executors

import scala.concurrent.duration.{Duration, DurationLong}
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * @author Michael
 */
class LedController(leds: Seq[DigitalPin], rgbs: Seq[DigitalPin]) extends PinController(leds ++ rgbs, Nil) {
  private val executor = Executors.newSingleThreadScheduledExecutor()
  implicit val ec = ExecutionContext.fromExecutor(executor)
  val ledPins = ppins take leds.size
  val rgbPins = ppins drop leds.size

  def enable(color: Color, leds: Int*) = {
    setColor(color)
    leds.map(ledPins.apply).foreach(_.enable())
  }

  def disable(led: Int) = ledPins(led).disable()

  def enableTimed(duration: Duration, color: Color, leds: Int*): Future[Seq[Unit]] = {
    enableTimed2(duration, color, leds.map(ledPins.apply): _*)
  }

  def enableTimed2(duration: Duration, color: Color, leds: ProvisionedDigitalPin*): Future[Seq[Unit]] = {
    setColor(color)
    Future.sequence(leds.map(_.enableTimed(duration)))
  }

  def enableTimedSync(duration: Duration, color: Color, leds: Int*): Unit = {
    val f = enableTimed(duration, color, leds: _*)
    Await.result(f, duration + 5.seconds)
  }

  def enableRandomColorSync(duration: Duration, leds: Int*): Unit =
    enableTimedSync(duration, Color.randomColor, leds: _*)

  def setColor(color: Color) = {
    // 3-length seq of rgb pinstates
    val Seq(redOp, greenOp, blueOp) = color.toSeq.map(b => (p: ProvisionedDigitalPin) => if (b) p.enable() else p.disable())
    val Seq(red, green, blue) = rgbPins
    redOp(red)
    greenOp(green)
    blueOp(blue)
  }

  override def close(): Unit = {
    super.close()
    executor.shutdown()
  }
}