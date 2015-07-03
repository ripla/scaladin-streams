package vaadin.scala.streams.source

import akka.stream.Materializer
import org.reactivestreams.{Subscriber, Publisher}
import vaadin.scala.Component

trait ComponentPublisher extends Component {

  lazy val enabledSource = new SourceHolder[Boolean](enabled_=)

  def enabledPublisher(implicit mat: Materializer): Publisher[Boolean] = enabledSource.publisher

  def enabledSubscriber(implicit mat: Materializer): Subscriber[Boolean] = enabledSource.subscriber
}
