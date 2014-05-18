package vaadin.scala.streams

import org.scalatest.FlatSpec
import akka.stream.{MaterializerSettings, FlowMaterializer}
import akka.stream.scaladsl.Flow
import vaadin.scala.{Property, UI}
import StreamImplicits._
import org.scalatest.mock.MockitoSugar
import akka.actor.ActorSystem

class PropertyOpsTest extends FlatSpec with MockitoSugar {

  behavior of "Property related implicits"

  it should "allow values to be produced to a property" in {
    implicit val ui = mock[UI]
    implicit val system = ActorSystem("Test")

    val materializer = FlowMaterializer(MaterializerSettings())
    val producer = Flow(() => 1).toProducer(materializer)
    val property: Property[Int] = Property(1)
    producer.produceTo(property)

    assert(property.value === Some(1))
  }

}
