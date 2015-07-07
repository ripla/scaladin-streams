package vaadin.scala.streams

import akka.stream.scaladsl.Sink
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}
import vaadin.scala.streams.generators.Name

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Very simple tests to see that files can be read etc.
 */
class NameGeneratorTest extends ScaladinStreamTest {

  behavior of "Name generator"

  it should "generate random names" in {
    val name = Name.getName()

    assert(name != null)
    assert(!name.isEmpty)
    Console.println(name)
  }

  it should "generate random names as a stream" in {
    val names = Name.getNames()

    val namesFuture: Future[Seq[String]] = names.grouped(3).runWith(Sink.head)

    val result = Await.result(namesFuture, 100 millis)

    assert(result != null)
    assert(!result.isEmpty)
    Console.println(result)
  }
}
