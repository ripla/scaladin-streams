package vaadin.scala.streams.subscriber

import vaadin.scala.UI

abstract class UISubscriber[T](implicit override val ui: UI) extends ScaladinSubscriber[T]