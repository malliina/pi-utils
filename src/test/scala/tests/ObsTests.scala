package tests

import com.mle.concurrent.Futures
import org.scalatest.FunSuite
import rx.lang.scala.Observable

import scala.concurrent.duration.{Duration, DurationLong}
import scala.concurrent.{Await, Future}

/**
 * @author Michael
 */
class ObsTests extends FunSuite {
  test("can run test") {
    assert(1 === 1)
  }
  test("Observable.interval first element arrives after first delay") {
    val start = System.currentTimeMillis()
    val s = Observable.interval(1 seconds).take(1).subscribe(i => {
      val dur = (System.currentTimeMillis() - start).millis
      val millis = dur.toMillis
      assert(millis > 800 && millis < 1200)
    })
    Thread.sleep(2.seconds.toMillis)
    s.unsubscribe()
  }
  test("Futures.after (delayed execution)") {
    delayTest((delay, func) => Futures.after(delay)(func))
  }

  def delayTest(f: (Duration, => Duration) => Future[Duration]) = {
    val start = System.currentTimeMillis()
    val durationFuture = f(2 seconds, (System.currentTimeMillis() - start).millis)
    val duration = Await.result(durationFuture, 3 seconds)
    val durMillis = duration.toMillis
    println(duration)
    assert(durMillis > 1800 && durMillis < 2200)
  }

}
