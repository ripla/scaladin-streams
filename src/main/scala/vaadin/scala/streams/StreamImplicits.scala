package vaadin.scala.streams

import akka.actor.ActorRef
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import org.reactivestreams.{Publisher, Subscriber}
import vaadin.scala._
import vaadin.scala.event.{ValueChangeEvent, ValueChangeNotifier}

import scala.concurrent.Future

object StreamImplicits {

  implicit class ValueChangeListener2Publisher[T](c: Property[T] with ValueChangeNotifier)(implicit mat: Materializer) {

    // create a flow from an Actor -> Publisher
    // connect the Actor to the value change events of the property
    val actorSource = Source.actorRef[Option[T]](1, OverflowStrategy.dropBuffer)
    val publisherSink = Sink.publisher[Option[T]]
    val (publisherActor: ActorRef, valuePublisher: Publisher[Option[T]]) = Flow[Option[T]].runWith(actorSource, publisherSink)
    val sourceNotifyingFunc: (ValueChangeEvent => Unit) = (e: ValueChangeEvent) => publisherActor ! e.property.value
    c.valueChangeListeners += sourceNotifyingFunc

    // create a flow from an Subscriber -> Function sink
    val subscriberSource = Source.subscriber[Option[T]]
    val functionSink = Sink.foreach[Option[T]](c.value_=)
    val (valueSubscriber: Subscriber[Option[T]], functionFuture: Future[Unit]) = Flow[Option[T]].runWith(subscriberSource, functionSink)

    def valueIn: Subscriber[Option[T]] = valueSubscriber

    def valueOut: Publisher[Option[T]] = valuePublisher


  }

}