package vaadin.scala.streams

import vaadin.scala.{UI, Property}
import org.reactivestreams.api.Consumer
import org.reactivestreams.spi.{Subscription, Subscriber}
import com.vaadin.server.ErrorEvent

object StreamImplicits {

  implicit class PropertyOps[T](property: Property[T])(implicit ui: UI) extends Consumer[T] {
    val internalSubscriber = new Subscriber[T] {

      var internalSubscription: Option[Subscription] = _

      override def onSubscribe(subscription: Subscription): Unit = {
        internalSubscription = Some(subscription)
        subscription.requestMore(1)
      }

      override def onError(cause: Throwable): Unit = ui.p.getErrorHandler.error(new ErrorEvent(cause))

      override def onComplete(): Unit = internalSubscription = None

      override def onNext(element: T): Unit = {
        Console.println(s"Got next value from stream: $element")
        ui.access {
          property.value = element
        }
        waitAndGetNext()
      }

      def waitAndGetNext() {
        Thread.sleep(500)
        internalSubscription.foreach(_.requestMore(1))
      }
    }

    override def getSubscriber: Subscriber[T] = internalSubscriber
  }
}