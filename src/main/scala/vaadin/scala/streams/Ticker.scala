package vaadin.scala.streams

import java.util.Date

import akka.actor.Cancellable
import akka.stream.scaladsl.Source

import scala.concurrent.duration._


object Ticker {

  val source :Source[Object, Cancellable] = Source(1 second, 1 second, new Object())
}
