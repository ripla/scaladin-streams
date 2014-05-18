package vaadin.scala.streams

import vaadin.scala._
import akka.stream.scaladsl.Flow
import scala.concurrent.forkjoin.ThreadLocalRandom
import akka.stream.{MaterializerSettings, FlowMaterializer}
import org.reactivestreams.api.Producer
import akka.actor.ActorSystem
import StreamImplicits._
import vaadin.scala.converter.Converter
import java.util.Locale

class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic) {

  implicit val system = ActorSystem("Streams")

  implicit val implicitui = this

  detachListeners += (event => system.shutdown())

  content = new VerticalLayout {
    margin = true
    val label = new Label
    val labelData = Property(0)
    label.property = labelData
    label converter = new Converter[String, Int]() {
      override def convertToPresentation(value: Option[Int], targetType: Class[_ <: String], locale: Locale): Option[String] = value map(v=> v.toString)

      override def convertToModel(value: Option[String], targetType: Class[_ <: Int], locale: Locale): Option[Int] = value map (v => v.toInt)
    }
    components += label

    val producer: Producer[Int] = simpleIntProducer

    producer.produceTo(labelData)
  }

  //akka streams
  def simpleIntProducer: Producer[Int] = {
    val materializer = FlowMaterializer(MaterializerSettings())
    Flow(() => ThreadLocalRandom.current().nextInt(5)).toProducer(materializer)
  }
}