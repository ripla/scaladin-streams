package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.vaadin.ui.themes.ValoTheme
import org.joda.time.{DurationFieldType, DateTime}
import org.reactivestreams.Subscriber
import vaadin.scala._
import vaadin.scala.streams.StreamImplicits._
import vaadin.scala.streams.generators.Ticker

class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic, theme = ValoTheme.THEME_NAME) {

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

    val dateLabel = Label("Loading")
    val tickLabel = Label("Loading")
    val backlog = new Grid()
    val sprintBacklog = new Grid()
    val events = new Grid()

    val dayTicker = Ticker.source

    val ticketToCount: Flow[Object, Int, Unit] = Flow[Object].scan(1)((prev: Int, ignored: Object) => prev + 1)
    val countToDate: Flow[Int, DateTime, Unit] = Flow[Int].scan(DateTime.now())((prev: DateTime, tick: Int) => prev.withFieldAdded(DurationFieldType.days(), 1))
    val stringify: Flow[Any, Option[String], Unit] = Flow[Any].map(anyVal => Option(anyVal).map(_.toString))

    val dateLabelSink: Subscriber[Option[String]] = dateLabel.valueIn

    dayTicker
      .via(ticketToCount)
      .via(countToDate)
      .via(stringify)
      .runWith(Sink(dateLabelSink))

    components += tickLabel
    components += dateLabel
  }
}