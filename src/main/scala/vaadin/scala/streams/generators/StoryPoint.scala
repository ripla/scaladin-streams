package vaadin.scala.streams.generators

import akka.stream.scaladsl.Source

object StoryPoint extends Generator {

  val points: List[Int] = List(1, 2, 3, 5, 13)

  def getPoint(): Int = pickRandom(points)

  def getPoints(): Source[Int, Unit] = Source(() => Iterator.continually(getPoint()))
}
