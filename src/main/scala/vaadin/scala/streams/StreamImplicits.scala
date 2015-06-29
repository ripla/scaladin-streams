package vaadin.scala.streams

import vaadin.scala.{UI, Property}
import org.reactivestreams.api.{Producer, Consumer}
import org.reactivestreams.spi.{Subscription, Publisher, Subscriber}
import vaadin.scala.streams.subscriber.{DelayedSubscriber, PropertySubscriber, ErrorHandlingSubscriber, UISubscriber}
import vaadin.scala.event.ValueChangeNotifier

import scala.collection.mutable

object StreamImplicits {

  implicit class Property2Consumer[T](p: Property[T])(implicit ui: UI) extends Consumer[T] {
    override def getSubscriber: Subscriber[T] = new UISubscriber[T] with DelayedSubscriber[T] with ErrorHandlingSubscriber[T] with PropertySubscriber[T] {
      override val property = p
    }
  }

  implicit class Property2Producer[T](p: Property[T] with ValueChangeNotifier)(implicit ui: UI) extends Producer[T] {

    val lock = ui.session.lockInstance

    def getPublisher: mutable.Publisher[T] = internalPublisher

    def produceTo(consumer: Consumer[T]) = getPublisher().subscribe(consumer.getSubscriber())

    val internalPublisher = new mutable.Publisher[T] {

      def inLock(action: => Unit): Unit = {
        lock.lock()
        try {
          action
        } finally {
          lock.unlock()
        }
      }

      var subscriptions: Map[mutable.Subscriber[T], Subscription] = Map.empty
      var valueRequests: Map[mutable.Subscriber[T], Int] = Map.empty

      p.valueChangeListeners += (event => inLock {
        valueRequests
          .filter { case (key, value) => value > 0}
          .map { case (key, value) => (key, value - 1)}
          .keys.foreach(k => k.onNext(event.property.value.get.asInstanceOf[T]))
      })


      override def subscribe(subscriber: Subscriber[T]): Unit = inLock {
        val subscription: Subscription = new Subscription {
          override def cancel(): Unit = {
            subscriptions = subscriptions - subscriber
            valueRequests = valueRequests - subscriber
          }

          override def requestMore(elements: Int): Unit = valueRequests = valueRequests + (subscriber -> (valueRequests.getOrElse(subscriber, 0) + 1))
        }

        subscriber.onSubscribe(subscription)

        subscriptions = subscriptions + (subscriber -> subscription)
      }
    }
  }


}