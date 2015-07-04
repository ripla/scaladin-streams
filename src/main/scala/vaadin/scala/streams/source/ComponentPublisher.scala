package vaadin.scala.streams.source

import akka.stream.scaladsl.Source
import vaadin.scala.Component

trait ComponentPublisher extends Component {

  lazy val enabledStream = new PropertyStream[Boolean](enabled_=)

  lazy val visibleStream = new PropertyStream[Boolean](visible_=)

}
