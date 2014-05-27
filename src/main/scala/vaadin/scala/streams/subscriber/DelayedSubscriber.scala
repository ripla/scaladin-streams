package vaadin.scala.streams.subscriber

trait DelayedSubscriber[T] extends ScaladinSubscriber[T] {

   def handlePressure(): Unit = Thread.sleep(500)
}
