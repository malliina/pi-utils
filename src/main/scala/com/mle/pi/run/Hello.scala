package com.mle.pi.run

import java.io.Closeable

import com.mle.pi.PinEvents.PinChangedEvent
import com.mle.pi._
import com.mle.rx.Observables
import com.mle.util.{Log, Utils}
import rx.lang.scala.{Observable, Observer}

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future, Promise}


/**
 *
 * @author Michael
 * @see http://pi4j.com/pins/model-b-rev2.html
 * @see http://pi4j.com/images/p1header-large.png
 */
object Hello extends Log {
  val LED01 = PiRevB2.PIN12
  val LED02 = PiRevB2.PIN16
  val LED03 = PiRevB2.PIN18
  val LED04 = PiRevB2.PIN22
  val LED05 = PiRevB2.PIN07
  val ledPins = LED01.high +: Seq(LED02, LED03, LED04, LED05).map(_.low)

  val LED_RED = PiRevB2.PIN11
  val LED_GREEN = PiRevB2.PIN13
  val LED_BLUE = PiRevB2.PIN15
  val rgbPins = Seq(LED_RED, LED_GREEN, LED_BLUE).map(_.high)

  val pinObserver = Observer[PinChangedEvent](
    (change: PinChangedEvent) => log.info("" + change),
    (err: Throwable) => log.warn("Error.", err),
    () => log.info("Completed!"))

  def main(args: Array[String]) {
    fiver()
  }

  def blaster(): Unit = {
    val b = new Blaster
    val num = PiRevB2.PIN07.gpioNumber
    PwmValue.from(200).map(v => {
      b.write(num, v)
      Thread sleep 5000
      b.release(num)
    })
  }

  def fiver(): Unit = {
    usingAsync(new PiRevB2)(pins => {
      pins.events subscribe pinObserver
      pins.PIN07.enableTimed(10.seconds)
    })
  }

  def redOne(): Unit = {
    withControllerAsync(ctrl => {
      ctrl.events subscribe pinObserver
      ctrl.rgbPins.head.enable()
      ctrl.ledPins(1).enableTimed(5.seconds)
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

  def withControllerAsync(f: LedController => Future[Any]) = usingAsync(new LedController(ledPins, rgbPins))(f)

  def withPins(f: PinController => Future[Any]) = usingAsync(new PinController(ledPins, Nil))(f)

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
