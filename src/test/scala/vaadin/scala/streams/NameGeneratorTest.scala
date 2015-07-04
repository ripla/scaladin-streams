package vaadin.scala.streams

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import vaadin.scala.streams.generators.Name

class NameGeneratorTest extends FlatSpec with MockitoSugar {

  behavior of "Name generator"

  it should "generate random names" in {
    val name = Name.getName()

    assert(name != null)
    assert(!name.isEmpty)
    Console.println(name)
  }
}
