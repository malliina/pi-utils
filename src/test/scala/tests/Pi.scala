package tests

import com.mle.pi.Color
import org.scalatest.FunSuite

/**
 * @author Michael
 */
class Pi extends FunSuite {
  test("light a LED for a short while") {
//    RgbLed.test()
  }
  test("color encoding"){
    val yellow = Color.yellow
    assert(yellow.red)
    assert(yellow.green)
    assert(!yellow.blue)
  }
}
