package vaadin.scala.streams.source

import akka.actor.ActorRef
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import org.reactivestreams.{Publisher, Subscriber, Subscription}

class SourceHolder[T](setter: T => Unit) {

  //lazily instantiated, but needs implicit Materializer
  private var sourceInternal: Source[T, ActorRef] = _
  private var sourceActorInternal: ActorRef = _

  def publisher(implicit mat: Materializer): Publisher[T] = source.runWith(Sink.publisher)

  def subscriber(): Subscriber[T] = {

    new Subscriber[T] {
      var sub: Subscription = _

      override def onError(t: Throwable): Unit = sub.cancel()

      override def onSubscribe(s: Subscription): Unit = {
        sub = s
        sub.request(1)
      }

      override def onComplete(): Unit = sub.cancel()

      override def onNext(t: T): Unit = {
        setter(t)
      }
    }
  }

  def source(implicit mat: Materializer): Source[T, ActorRef] = {
    if (sourceInternal == null) {
      sourceInternal = createSource()
    }

    sourceInternal
  }

  def sourceActor(implicit mat: Materializer): ActorRef = {
    if (sourceActorInternal == null) {
      sourceActorInternal = createSourceFlow(source)
    }

    sourceActorInternal
  }

  private def createSource(): Source[T, ActorRef] =
    Source.actorRef[T](1, OverflowStrategy.dropBuffer)


  private def createSourceFlow(source: Source[T, ActorRef])(implicit mat: Materializer): ActorRef =
    Flow[T]
      .to(Sink.ignore)
      .runWith(source)
}
