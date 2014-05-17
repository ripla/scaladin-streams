package vaadin.scala.streams

import vaadin.scala._
import akka.stream.scaladsl.Flow
import scala.concurrent.forkjoin.ThreadLocalRandom
import akka.stream.{MaterializerSettings, FlowMaterializer}
import org.reactivestreams.api.{Consumer, Producer}
import org.reactivestreams.spi.{Subscription, Subscriber}
import akka.actor.ActorSystem


class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic) {

  implicit val system = ActorSystem("Streams")

  detachListeners += (event => system.shutdown())

    content = new VerticalLayout {
      margin = true
      val label = new Label with Consumer[Int] {

        value = "This Label is about to get streamed!"

        val internalSubscriber = new Subscriber[Int] {

          var internalSubscription: Option[Subscription] = _

          override def onSubscribe(subscription: Subscription): Unit = {
            internalSubscription = Some(subscription)
            subscription.requestMore(1)
          }

          override def onError(cause: Throwable): Unit = ???

          override def onComplete(): Unit = internalSubscription = None

          override def onNext(element: Int): Unit = {
            Console.println(s"Got next value from stream: $element")
            ui.access {
              value = element.toString
            }
            waitAndGetNext()
          }

          def waitAndGetNext() {
            Thread.sleep(500)
            internalSubscription.foreach(_.requestMore(1))
          }
        }

        override def getSubscriber: Subscriber[Int] = internalSubscriber
      }

      components += label

      val producer: Producer[Int] = simpleIntProducer

      producer.produceTo(label)
    }

    //akka streams
    def simpleIntProducer: Producer[Int] = {
      val materializer = FlowMaterializer(MaterializerSettings())
      Flow(() => ThreadLocalRandom.current().nextInt(5)).toProducer(materializer)
    }
  }