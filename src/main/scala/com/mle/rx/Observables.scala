package com.mle.rx

import rx.lang.scala.Observable

/**
 * @author Michael
 */
object Observables {
  def mergeAll[T](events: Seq[Observable[T]]) =
    events.foldLeft[Observable[T]](Observable.empty)((acc, elem) => acc merge elem)
}
