package vaadin.scala.streams

import java.util.Date

import akka.actor.{ActorSystem, Cancellable}
import akka.event.Logging
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.scaladsl.{Sink, Source}
import org.reactivestreams.Subscriber
import vaadin.scala._
import vaadin.scala.streams.StreamImplicits._

import scala.concurrent.duration._

class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic) {

  implicit val system = ActorSystem("Streams")
  implicit var actorMaterializer: ActorMaterializer = ActorMaterializer()
  implicit val thisUI: vaadin.scala.UI = this

  detachListeners += (event => system.shutdown())

  content = new VerticalLayout {

    margin = true

    val label = Label("Loading...")

    val timeTicker: Source[Date, Cancellable] = Source(1.second, 1.second, new Object()).map(t => new Date)

    val labelSubscriber: Subscriber[Option[String]] = label.valueIn

    timeTicker
      .log("Dateticker").withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel, onFailure = Logging.ErrorLevel))
      .map(date => Some(date.toString))
      .runWith(Sink(labelSubscriber))

    components += label

    components += Button(caption = "Print state",  clickListener = Console.println(label.value))
  }
}