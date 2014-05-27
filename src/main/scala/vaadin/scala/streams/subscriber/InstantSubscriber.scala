package vaadin.scala.streams.subscriber

trait InstantSubscriber[T] extends UISubscriber[T] {

  override def handlePressure(): Unit = {}
}