package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, BeforeAndAfter}

trait ScaladinStreamTest extends FlatSpec with MockitoSugar with BeforeAndAfter {

  implicit var actorSystem: ActorSystem = _
  implicit var actorMaterializer: ActorMaterializer = _

  before {
    actorSystem = ActorSystem()
    actorMaterializer = ActorMaterializer()
  }

  after {
    actorSystem.shutdown()
  }
}
