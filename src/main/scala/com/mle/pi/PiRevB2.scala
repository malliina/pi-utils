package com.mle.pi

/**
 * @author Michael
 */
class PiRevB2 extends PinController(PiRevB2.gpios) {
  val Seq(io00, io01, io02, io03, io04, io05, io06, io07) = ppins

  val PIN07 = io07
  val PIN11 = io00
  val PIN12 = io01
  val PIN13 = io02
  val PIN15 = io03
  val PIN16 = io04
  val PIN18 = io05
  val PIN22 = io06

//  def snapshot = ppins.map(prov => PinInfo(prov.number, prov.outPin.getState, prov.enableState))
}

object PiRevB2 {

  import com.pi4j.io.gpio.RaspiPin._

  val pins = Seq(
    GPIO_00 -> 11,
    GPIO_01 -> 12,
    GPIO_02 -> 13,
    GPIO_03 -> 15,
    GPIO_04 -> 16,
    GPIO_05 -> 18,
    GPIO_06 -> 22,
    GPIO_07 -> 7
  ).map {
    case (pin, num) => MappedPin(pin, num)
  }.toSeq
  val gpios = pins map (_.high)
}

