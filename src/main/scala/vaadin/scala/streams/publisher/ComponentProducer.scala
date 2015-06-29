package vaadin.scala.streams.publisher

import java.util.concurrent.ThreadLocalRandom

import akka.stream.MaterializerSettings
import akka.stream.scaladsl.Flow
import org.reactivestreams.api.Producer
import vaadin.scala.Component

trait ComponentProducer {
  self: Component =>

  lazy val enabledProducer:Producer[Boolean] = {
    import akka.stream.FlowMaterializer
    val materializer = FlowMaterializer(MaterializerSettings())
    Flow(() => ThreadLocalRandom.current().nextInt(5)).toProducer(materializer)
  }

  def enabled(): Producer[Boolean] = {
    import akka.stream.FlowMaterializer
    val materializer = FlowMaterializer(MaterializerSettings())
    Flow(() => ThreadLocalRandom.current().nextInt(5)).toProducer(materializer)

    self.enabled
  }


}
