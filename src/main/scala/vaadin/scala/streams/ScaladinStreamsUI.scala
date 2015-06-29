package vaadin.scala.streams

import java.util.concurrent.ThreadLocalRandom

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.stream.{FlowMaterializer, MaterializerSettings}
import vaadin.scala._
import vaadin.scala.converter._

class ScaladinStreamsUI extends UI(title = "Scaladin Streams", pushMode = PushMode.Automatic) {

  implicit val system = ActorSystem("Streams")

  implicit val implicitui = this

  val lock = ui.session.lockInstance

  detachListeners += (event => system.shutdown())

  content = new VerticalLayout {

    margin = true
    val label = new Label
    val labelProperty = Property(0)
    label.property = labelProperty
    label.converter = new Converter[String, Int]() {

      import java.util.Locale

      override def convertToPresentation(value: Option[Int], targetType: Class[_ <: String], locale: Locale): Option[String] = value map (v => v.toString)

      override def convertToModel(value: Option[String], targetType: Class[_ <: Int], locale: Locale): Option[Int] = value map (v => v.toInt)
    }

    components += label

    val producer: Producer[Int] = simpleIntProducer

    producer.produceTo(labelProperty)

    val slider = new HorizontalSlider {
      width = 500 px
    }
    /*components += slider*/
    toIntProducer(slider).produceTo(labelProperty)
  }

  //akka streams
  def simpleIntProducer: Producer[Int] = {
    import akka.stream.FlowMaterializer
    val materializer = FlowMaterializer(MaterializerSettings())
    Flow(() => ThreadLocalRandom.current().nextInt(5)).toProducer(materializer)
  }

  def toIntProducer(p: Producer[java.lang.Double]): Producer[Int] = {
    val materializer = FlowMaterializer(MaterializerSettings())
    Flow(p).map(value => value.toInt).toProducer(materializer)
  }
}