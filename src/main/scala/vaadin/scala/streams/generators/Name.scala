package vaadin.scala.streams.generators

import akka.stream.scaladsl.Source

object Name extends Generator {

  def getName(): String = createName(pickRandom(firstNames), pickRandom(lastNames))

  def getNames(): Source[String, Unit] = Source(() => Iterator.continually(getName()))

  val sourceLines: List[(String, String)] = readCsvLines("names.csv")
    .map(_.split(" "))
    .map(arrayToTuple)
    .toList

  val firstNames = sourceLines.map { case (firstName, lastName) => firstName }
  val lastNames = sourceLines.map { case (firstName, lastName) => lastName }

  private def createName(first: String, last: String) = s"$first $last"


}
