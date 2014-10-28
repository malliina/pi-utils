package com.mle.pi

/**
 * @author Michael
 */
class PiRevB2 extends PinController(PiRevB2.gpios, Seq(PiRevB2.pwm)) {
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

  import com.pi4j.io.gpio.RaspiPin._

  val digitals = Seq(
    PinPlan(GPIO_00, 11),
    //    MappedPin(GPIO_01, 12),
    PinPlan(GPIO_02, 13),
    PinPlan(GPIO_03, 15),
    PinPlan(GPIO_04, 16),
    PinPlan(GPIO_05, 18),
    PinPlan(GPIO_06, 22),
    PinPlan(GPIO_07, 7)
  )
  val gpios = digitals map (_.high)
  val pwm = PwmPin(GPIO_01, 12)
}

