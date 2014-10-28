package com.mle.pi

import com.pi4j.io.gpio.Pin

/**
 * @author Michael
 */
case class PwmPin(p: Pin, num: Int) extends MappedPin(p, num)