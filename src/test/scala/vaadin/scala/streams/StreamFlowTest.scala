package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import org.reactivestreams.Subscriber
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}

import scala.concurrent.Await
import scala.concurrent.duration._

class StreamFlowTest extends FlatSpec with MockitoSugar with BeforeAndAfter {

  behavior of "Stream flows"

  implicit var actorSystem: ActorSystem = _
  implicit var actorMaterializer: ActorMaterializer = _

  case class FlowTest(var value: Boolean) {
    def setValue(newValue: Boolean) {
      value = newValue
    }
  }

  before {
    actorSystem = ActorSystem()
    actorMaterializer = ActorMaterializer()
  }

  after {
    actorSystem.shutdown()
  }

  it should "pass messages from the subscriber to the given function" in {
    val testPojo = FlowTest(value = false)

    val subscriberSource: Source[Boolean, Subscriber[Boolean]] = Source.subscriber[Boolean]
    val pojoModifyingSink = Sink.foreach(testPojo.setValue)

    val flow: RunnableGraph[Subscriber[Boolean]] = subscriberSource.to(pojoModifyingSink)

    //create Reactive Streams Subscriber
    val subscriber: Subscriber[Boolean] = flow.run()

    Source.single(true).to(Sink(subscriber)).run()

    Thread.sleep(100)

    assert(testPojo.value === true)
  }

  it should "pass messages from the subscriber to the given function and output to publisher" in {
    val testPojo = FlowTest(value = false)
    var testValue = false

    val subscriberSource = Source.subscriber[Boolean]
    val pojoModifyingSink = Sink.foreach(testPojo.setValue)
    val publisherSink = Sink.fanoutPublisher[Boolean](initialBufferSize = 4, maximumBufferSize = 16)

    val graph = FlowGraph.closed(subscriberSource, publisherSink)(Keep.both) { implicit builder =>
      (in, out) =>
        import FlowGraph.Implicits._

        val broadcast = builder.add(Broadcast[Boolean](2))

        in ~> broadcast ~> out
              broadcast ~> pojoModifyingSink
    }

    //create Reactive Streams Subscriber, Publisher
    val (subscriber, publisher) = graph.run()

    val publisherFuture = Source(publisher).runWith(Sink.head)

    Source.single(true).to(Sink(subscriber)).run()

    Thread.sleep(100)

    assert(testPojo.value === true)

    val publisherResult = Await.result(publisherFuture, 100 millis)

    assert(publisherResult === true)
  }

}
