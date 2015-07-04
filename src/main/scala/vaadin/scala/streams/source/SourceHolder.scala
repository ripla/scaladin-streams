package vaadin.scala.streams.source

import akka.stream.Materializer
import akka.stream.scaladsl._
import org.reactivestreams.{Subscriber, Publisher}

class SourceHolder[T](setter: T => Unit) {

  private var _publisher: Publisher[T] = _
  private var _subscriber: Subscriber[T] = _

  def publisher(implicit mat: Materializer): Publisher[T] = {
    if (_publisher == null) {
      initFlow()
    }

    _publisher
  }

  def subscriber(implicit mat: Materializer): Subscriber[T] = {
    if (_subscriber == null) {
      initFlow()
    }

    _subscriber
  }

  private def initFlow()(implicit mat: Materializer): Unit = {
    val subscriberSource = Source.subscriber[T]
    val pojoModifyingSink = Sink.foreach(setter)
    val publisherSink = Sink.fanoutPublisher[T](initialBufferSize = 4, maximumBufferSize = 16)

    val graph = FlowGraph.closed(subscriberSource, publisherSink)(Keep.both) { implicit builder =>
      (subscriber, publisher) =>
        import FlowGraph.Implicits._

        val broadcast = builder.add(Broadcast[T](2))

        subscriber ~> broadcast ~> publisher
                      broadcast ~> pojoModifyingSink
    }

    val result: (Subscriber[T], Publisher[T]) = graph.run()

    _subscriber = result._1
    _publisher = result._2
  }
}
