package vaadin.scala.streams.generators

import akka.stream.scaladsl.Source

import scala.util.Random

object UserStory extends FromCsvGenerator {

  Random.nextInt()

  def getStory(): String = createStory(pickRandom(whoLines), pickRandom(whatLines), pickRandom(whyLines))

  def getStories(): Source[String, Unit] = Source(() => Iterator.continually(getStory()))

  private def createStory(who: String, what: String, why: String) = s"As a $who, I want to $what, so that I can $why"

  val whatLines: List[String] = readCsvLines("whats.csv")
    .map(line => line.filter(char => char != ','))
    .toList

  val whoLines: List[String] = readCsvLines("whos.csv")
    .map(line => line.filter(char => char != ','))
    .toList

  val whyLines: List[String] = readCsvLines("whys.csv")
    .map(line => line.filter(char => char != ','))
    .toList
}
