package vaadin.scala.streams

import akka.actor.{ActorSystem, TypedActor, TypedProps}
import org.reactivestreams.Publisher
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import vaadin.scala.{Component, TextField}


class ComponentOpsTest extends FlatSpec with MockitoSugar {

  behavior of "Component implicits"

  it should "allow producing a boolean stream from Component.isEnabled" in {
    val textfield = new TextField()


    val system: ActorSystem = ActorSystem()

    implicit class Foo(c: Component) {

      def enabled: Producer[Boolean] = new Producer[Boolean] {

        val internalPublisher: Publisher[Boolean] = TypedActor(system).typedActorOf(TypedProps(classOf[Publisher[Boolean]], new Publisher[Boolean] {
          override def subscribe(subscriber: Subscriber[Boolean]): Unit = {

          }
        }))

        override def getPublisher: Publisher[Boolean] = internalPublisher

        override def produceTo(consumer: Consumer[Boolean]): Unit = internalPublisher.subscribe(consumer.getSubscriber)
      }
    }


    val producer: Producer[Boolean] = textfield.enabled

  }
}
