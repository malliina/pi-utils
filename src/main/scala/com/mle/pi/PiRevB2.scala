package com.mle.pi

/**
 * @author Michael
 */
class PiRevB2 extends PinController(PiRevB2.highDigitals, Seq(PiRevB2.pwm)) {
  val Seq(io00, io02, io03, io04, io05, io06, io07) = ppins

  val PIN07 = io07
  val PIN11 = io00
  val PIN12 = ppwms.head
  val PIN13 = io02
  val PIN15 = io03
  val PIN16 = io04
  val PIN18 = io05
  val PIN22 = io06
  val pwm = PIN12

  //  def snapshot = ppins.map(prov => PinInfo(prov.number, prov.outPin.getState, prov.enableState))
}

object PiRevB2 {
  // model B rev 2 layout: http://pi4j.com/pins/model-b-rev2.html

  import com.pi4j.io.gpio.RaspiPin._

  val PIN07 = PinPlan(GPIO_07, 7, 4)
  val PIN11 = PinPlan(GPIO_00, 11, 17)
  val PIN12 = PinPlan(GPIO_01, 12, 18)
  val PIN13 = PinPlan(GPIO_02, 13, 21)
  val PIN15 = PinPlan(GPIO_03, 15, 22)
  val PIN16 = PinPlan(GPIO_04, 16, 23)
  val PIN18 = PinPlan(GPIO_05, 18, 24)
  val PIN22 = PinPlan(GPIO_06, 22, 25)
  val pins = Seq(PIN07, PIN11, PIN12, PIN13, PIN15, PIN16, PIN18, PIN22)

  val digitals = Seq(PIN11, PIN13, PIN15, PIN16, PIN18, PIN22, PIN07)
  val highDigitals = digitals map (_.high)
  val pwm = PIN12.pwm
}

