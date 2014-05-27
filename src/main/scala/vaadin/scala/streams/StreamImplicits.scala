package vaadin.scala.streams

import vaadin.scala.{UI, Property}
import org.reactivestreams.api.Consumer
import org.reactivestreams.spi.{Subscription, Subscriber}
import com.vaadin.server.ErrorEvent
import vaadin.scala.streams.subscriber.{DelayedSubscriber, PropertySubscriber, ErrorHandlingSubscriber, UISubscriber}

object StreamImplicits {

  implicit class PropertyOps[T](p: Property[T])(implicit ui: UI) extends Consumer[T] {
    override def getSubscriber: Subscriber[T] = new UISubscriber[T] with DelayedSubscriber[T] with ErrorHandlingSubscriber[T] with PropertySubscriber[T] {
      override val property = p
    }
  }
}