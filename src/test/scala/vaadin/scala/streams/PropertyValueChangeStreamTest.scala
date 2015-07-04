package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.reactivestreams.Publisher
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}
import vaadin.scala._
import vaadin.scala.streams.StreamImplicits._

import scala.concurrent.Await
import scala.concurrent.duration._


class PropertyValueChangeStreamTest extends FlatSpec with MockitoSugar with BeforeAndAfter {

  behavior of "Property value streaming"

  implicit var actorSystem: ActorSystem = _
  implicit var actorMaterializer: ActorMaterializer = _

  before {
    actorSystem = ActorSystem()
    actorMaterializer = ActorMaterializer()
  }

  after {
    actorSystem.shutdown()
  }

  it should "generate a stream of values from a property" in {
    val textfield = new TextField

    val valuePublisher: Publisher[Option[String]] = textfield.valueOut

    val streamFuture = Source(valuePublisher).map(_.get.toUpperCase()).runWith(Sink.head)

    textfield.value = "foo"

    val result = Await.result(streamFuture, 100 millis)

    assert(result === "FOO")
  }
}
