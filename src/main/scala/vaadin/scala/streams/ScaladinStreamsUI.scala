package vaadin.scala.streams

import akka.actor.ActorSystem
import vaadin.scala._

class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic) {

  implicit val system = ActorSystem("Streams")

  detachListeners += (event => system.shutdown())

  content = new VerticalLayout {

    margin = true
    val label = Label("hello")
  }
}