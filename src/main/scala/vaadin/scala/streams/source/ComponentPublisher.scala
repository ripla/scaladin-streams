package vaadin.scala.streams.source

import akka.stream.Materializer
import org.reactivestreams.{Subscriber, Publisher}
import vaadin.scala.Component

trait ComponentPublisher extends Component {

  lazy val enabledSource = new SourceHolder[Boolean](enabled_=)

}
