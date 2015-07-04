package vaadin.scala.streams

import akka.actor.ActorRef
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import org.reactivestreams.{Publisher, Subscriber}
import vaadin.scala._
import vaadin.scala.event.{ValueChangeEvent, ValueChangeNotifier}

import scala.concurrent.Future

object StreamImplicits {

  implicit class ValueChangeNotifier2Publisher[T](c: ValueChangeNotifier)(implicit mat: Materializer) {
    // create a flow from an Actor -> Publisher
    // connect the Actor to the value change events of the property
    val actorSource = Source.actorRef[Option[T]](1, OverflowStrategy.dropBuffer)
    val publisherSink = Sink.publisher[Option[T]]
    val (publisherActor: ActorRef, valuePublisher: Publisher[Option[T]]) = Flow[Option[T]].runWith(actorSource, publisherSink)
    val sourceNotifyingFunc: (ValueChangeEvent => Unit) = (e: ValueChangeEvent) => publisherActor ! e.property.value
    c.valueChangeListeners += sourceNotifyingFunc

    def valueOut: Publisher[Option[T]] = valuePublisher
  }

  implicit class Property2Subscriber[T](c: Property[T])(implicit mat: Materializer, ui: UI) {
    // create a flow from an Subscriber -> Function sink
    val subscriberSource = Source.subscriber[Option[T]]
    val functionSink = Sink.foreach[Option[T]](e => {
      ui.access(c.value_=(e))
    })
    val (valueSubscriber: Subscriber[Option[T]], functionFuture: Future[Unit]) = Flow[Option[T]].runWith(subscriberSource, functionSink)

    def valueIn: Subscriber[Option[T]] = valueSubscriber
  }

}