package com.mle.pi

/**
 * @author Michael
 */
private class RangedValue {

}

object RangedValue {
  def between(min: Int, max: Int) = new ValueBuilder(min, max)
}

class ValueBuilder(min: Int, max: Int) extends Validator[Int](i => i >= min && i <= max)

class Validator[T](p: T => Boolean) {
  def apply(attempt: T): Option[T] = Option(attempt).filter(p)
}