package com.mle.pi

import java.io.Closeable

import com.mle.util.Utils
import rx.lang.scala.Observable

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future, Promise}


/**
 *
 * @author Michael
 * @see http://pi4j.com/pins/model-b-rev2.html
 * @see http://pi4j.com/images/p1header-large.png
 */
object Hello {

  import com.pi4j.io.gpio.RaspiPin._

  // model B rev 2 layout: http://pi4j.com/pins/model-b-rev2.html
  val PIN07 = GPIO_07
  val PIN11 = GPIO_00
  val PIN12 = GPIO_01
  val PIN13 = GPIO_02
  val PIN15 = GPIO_03
  val PIN16 = GPIO_04
  val PIN18 = GPIO_05
  val PIN22 = GPIO_06

  val LED01 = MappedPin(GPIO_01, 12)
  val LED02 = MappedPin(GPIO_04, 16)
  val LED03 = MappedPin(GPIO_05, 18)
  val LED04 = MappedPin(GPIO_06, 22)
  val LED05 = MappedPin(GPIO_07, 7)
  val ledPins = Seq(LED01, LED02, LED03, LED04, LED05).map(_.low)

  val LED_RED = MappedPin(GPIO_00, 11)
  val LED_GREEN = MappedPin(GPIO_02, 13)
  val LED_BLUE = MappedPin(GPIO_03, 15)
  val rgbPins = Seq(LED_RED, LED_GREEN, LED_BLUE).map(_.high)

  def main(args: Array[String]) {
    fiver()
  }

  def fiver(): Unit = {
    usingAsync(new PiRevB2)(pins => {
      //      val sub = pins.events.subscribe(change => println(change))
      pins.PIN07.enableTimed(10.seconds)
    })
  }

  def redOne(): Unit = {
    withControllerAsync(ctrl => {
      ctrl.rgbPins.head.enable()
      ctrl.ledPins.head.enableTimed(5.seconds)
    })
  }

  def rainbowFaster() {
    looped(clock => ctrl => onTick(clock, ctrl))
  }

  def rainbowSlower() {
    withController(ctrl => {
      val p = Promise[Long]()
      Observable.interval(1.millis).map(_.toInt).take(20000).subscribe(
        tick => onTick(tick, ctrl),
        err => p.tryFailure(err),
        () => p.trySuccess(0))
      Await.result(p.future, 100 seconds)
    })
  }

  private def onTick(tick: Long, ctrl: LedController) = {
    ctrl.disableAll()
    val ledIndex = tick.toInt % 5
    val colorIndex = ledIndex
    ctrl.enable(Color.allColors(colorIndex), ledIndex)
  }

  def orangeTest() {
    looped(clock => ctrl => {
      val color = if (clock % 2 == 0) Color.red else Color.yellow
      ctrl setColor color
    })
  }

  def withController(f: LedController => Unit) = Utils.using(new LedController(ledPins, rgbPins))(f)

  def withPins(f: PinController => Future[Any]) = usingAsync(new PinController(ledPins, Nil))(f)

  def withControllerAsync(f: LedController => Future[Any]) = usingAsync(new LedController(ledPins, rgbPins))(f)

  def usingAsync[T <: Closeable](resource: T)(f: T => Future[Any]) = Utils.using(resource)(res => {
    val fut = f(res)
    Await.ready(fut, 60.minutes)
  })

  def looped(f: Long => LedController => Unit) = withController(ctrl => {
    val runDuration = 20.seconds
    val sleepTime = 1.millis.toMillis
    val runMillis = runDuration.toMillis
    var clock = 0L
    while (clock < runMillis) {
      f(clock)(ctrl)
      Thread sleep sleepTime
      clock += sleepTime
    }
  })
}
