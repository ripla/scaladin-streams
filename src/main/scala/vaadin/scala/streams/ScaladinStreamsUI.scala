package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorMaterializer, Attributes}
import org.reactivestreams.Subscriber
import vaadin.scala._
import vaadin.scala.streams.StreamImplicits._

class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic) {

  implicit val system = ActorSystem("Streams")
  implicit var actorMaterializer: ActorMaterializer = ActorMaterializer()
  implicit val thisUI: vaadin.scala.UI = this

  detachListeners += {
    event => {
      Console.println(s"Detaching UI $event")
      system.shutdown()
    }
  }

  content = new VerticalLayout {

    margin = true

    val label = Label("Loading...")

    val dayTicker = Ticker.source

    val tickerToDay = Flow[Object].scan(1)((prev: Int, ignored: Object) => prev + 1)
    val stringify: Flow[Any, Option[String], Unit] = Flow[Any].map(anyVal => Option(anyVal).map(_.toString))


    val labelSubscriber: Subscriber[Option[String]] = label.valueIn

    dayTicker
      .via(tickerToDay)
      .via(stringify)
      .log("Dayticker").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel, onFailure = Logging.ErrorLevel))
      .runWith(Sink(labelSubscriber))

    components += label
  }
}