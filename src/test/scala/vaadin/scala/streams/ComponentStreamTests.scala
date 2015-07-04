package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.TestSubscriber.Probe
import akka.stream.testkit.scaladsl.TestSink
import org.reactivestreams.Publisher
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}
import vaadin.scala._
import vaadin.scala.streams.source.ComponentPublisher


class ComponentStreamTests extends FlatSpec with MockitoSugar with BeforeAndAfter {

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
    textfield.enabled = false

    val enabledPublisher: Publisher[Boolean] = textfield.enabledStream.publisher

    val enabledSource: Probe[Boolean] = Source(enabledPublisher)
      .runWith(TestSink.probe[Boolean])

    Source.single(true).to(Sink(textfield.enabledStream.subscriber)).run()

    // assert
    enabledSource.requestNext(true)
    assert(textfield.enabled === true)
  }

  it should "allow connecting a stream from enabled to visibility" in {
    val button1 = new Button() with ComponentPublisher
    val button2 = new Button() with ComponentPublisher

    button2.visible = false

    val enabledSource =  Source(button1.enabledStream.publisher)
    val visibilitySink = Sink(button2.visibleStream.subscriber)

    enabledSource.map(!_).to(visibilitySink).run()

    Source.single(false).to(Sink(button1.enabledStream.subscriber)).run()

    // assert
    Thread.sleep(100)
    assert(button2.visible === true)
  }
}
