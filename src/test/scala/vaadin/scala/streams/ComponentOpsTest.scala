package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.reactivestreams.Publisher
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}
import vaadin.scala.TextField
import vaadin.scala.streams.source.ComponentPublisher


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
  /*  val textfield = new TextField() with ComponentPublisher

    val enabledSource: Publisher[Boolean] = textfield.enabled

    val source = Source(enabledSource)
      .runWith(TestSink.probe[Boolean])
      .request(1)

    textfield.enabled_=(true)(actorMaterializer)

    source.expectNext(true)*/
  }
}
