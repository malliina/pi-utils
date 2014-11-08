package com.mle.pi

import com.mle.values.{RangedInt, WrappedValue}

/**
 * @author Michael
 */
case class PwmValue private(value: Int) extends WrappedValue[Int]

object PwmValue extends RangedInt[PwmValue](min = 0, max = 1000) {
  override protected def build(t: Int): PwmValue = PwmValue(t)
}