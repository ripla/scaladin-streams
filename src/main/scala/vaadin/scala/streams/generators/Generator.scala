package vaadin.scala.streams.generators

import scala.io.{Source => IOSource}
import scala.util.Random

trait Generator {
  protected def pickRandom[T](list: List[T]): T = list(Random.nextInt(list.length))

  protected def arrayToTuple[T](arr: Array[T]): (T, T) = arr match {
    case Array(first, second) => (first, second)
  }

  protected def readCsvLines(filename: String) = IOSource
    .fromInputStream(getClass().getResourceAsStream("/" + filename))
    .getLines()
    .map(line => line.filter(char => char != ','))
}
