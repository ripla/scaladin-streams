package vaadin.scala.streams.source

import akka.actor.ActorRef
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.reactivestreams.Publisher

object PropertyPublisher {

  def createSource[T](): Source[T, ActorRef] =
    Source.actorRef[T](1, OverflowStrategy.dropBuffer)


  def createSourceFlow[T](source: Source[T, ActorRef])(implicit mat: Materializer): ActorRef =
    Flow[T]
      .to(Sink.ignore)
      .runWith(source)

  def getPublisher[T](source: Source[T, ActorRef])(implicit mat: Materializer): Publisher[T] = source.runWith(Sink.publisher)
}

trait PropertyPublisher {

}
