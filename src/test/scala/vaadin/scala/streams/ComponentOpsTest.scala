package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.TestSubscriber.Probe
import akka.stream.testkit.scaladsl.TestSink
import org.reactivestreams.Publisher
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}
import vaadin.scala.TextField
import vaadin.scala.streams.source.ComponentPublisher

import scala.concurrent.Await

import scala.concurrent.duration._


class ComponentOpsTest extends FlatSpec with MockitoSugar with BeforeAndAfter {

  behavior of "Component streaming"

  implicit var actorSystem: ActorSystem = _
  implicit var actorMaterializer: ActorMaterializer = _

  before {
    actorSystem = ActorSystem()
    actorMaterializer = ActorMaterializer()
  }

  after {
    actorSystem.shutdown()
  }

  it should "allow producing a boolean stream from Component.isEnabled" in {
    val textfield = new TextField() with ComponentPublisher

    val enabledPublisher: Publisher[Boolean] = textfield.enabledSource.publisher

    val enabledSource: Probe[Boolean] = Source(enabledPublisher)
      .runWith(TestSink.probe[Boolean])

    Source.single(true).to(Sink(textfield.enabledSource.subscriber)).run()

    // assert
    enabledSource.requestNext(true)
  }
}
