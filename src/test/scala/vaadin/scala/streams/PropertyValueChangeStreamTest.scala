package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.reactivestreams.{Publisher, Subscriber}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}
import vaadin.scala._
import vaadin.scala.streams.StreamImplicits._

import scala.concurrent.Await
import scala.concurrent.duration._
import org.mockito.Mockito._
import org.mockito.Matchers._

class PropertyValueChangeStreamTest extends ScaladinStreamTest {

  behavior of "Property value streaming"

  implicit val accessingUI: UI = new UI() {}

  it should "generate a stream of values from a property" in {
    val textfield = new TextField()

    val valuePublisher: Publisher[Option[String]] = textfield.valueOut

    val streamFuture = Source(valuePublisher).map(_.get.toUpperCase()).runWith(Sink.head)

    textfield.value = "foo"

    val result = Await.result(streamFuture, 100 millis)

    assert(result === "FOO")
  }

  it should "allow streaming values to a property" in {
    val textfield = new TextField
    textfield.value = None

    val valueSubscriber: Subscriber[Option[String]] = textfield.valueIn

    Source.single("foo").map(s => Some(s.toUpperCase())).runWith(Sink(valueSubscriber))

    Thread.sleep(200)

    assert(textfield.value === Some("FOO"))
  }
}
