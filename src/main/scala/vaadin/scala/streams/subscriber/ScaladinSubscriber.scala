package vaadin.scala.streams.subscriber

import org.reactivestreams.spi.{Subscription, Subscriber}
import vaadin.scala.UI

trait ScaladinSubscriber[T] extends Subscriber[T] {

  def ui: UI

  var internalSubscription: Option[Subscription] = _

  override def onSubscribe(subscription: Subscription): Unit = {
    internalSubscription = Some(subscription)
    getNextValues()
  }


  override def onComplete(): Unit = internalSubscription = None

  override def onError(cause: Throwable): Unit = Console.println(s"From stream: $internalSubscription, got error: $cause")

  override def onNext(element: T): Unit = {
    handlePressure()
    getNextValues()
  }

  def handlePressure(): Unit

  def getNextValues() = internalSubscription.foreach(_.requestMore(1))
}
