package com.mle.pi

/**
 * @author Michael
 */
case class PwmPin(plan: PinPlan) extends MappedPin(plan.pin, plan.boardNumber)