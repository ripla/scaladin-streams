package vaadin.scala.streams.generators

import scala.io.Source
import scala.util.Random

object Name {

  def getName(): String = createName(pickRandom(firstNames), pickRandom(lastNames))

  val sourceLines: List[(String, String)] = Source
    .fromInputStream(getClass().getResourceAsStream("/names.csv"))
    .getLines()
    .map(line => line.filter(char => char != ','))
    .map(_.split(" "))
    .map(arrayToTuple)
    .toList

  val firstNames = sourceLines.map{case (firstName, lastName) => firstName}
  val lastNames = sourceLines.map{case (firstName, lastName) => lastName}

  private def createName(first: String, last: String) = s"$first $last"

  private def arrayToTuple[T](arr: Array[T]): (T, T) = arr match {
    case Array(first, second) => (first, second)
  }

  private def pickRandom(list: List[String]): String = list(Random.nextInt(list.length))
}
