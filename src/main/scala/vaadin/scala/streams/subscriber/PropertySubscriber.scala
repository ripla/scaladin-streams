package vaadin.scala.streams.subscriber

import vaadin.scala.Property

trait PropertySubscriber[T] extends ScaladinSubscriber[T]{

  def property: Property[T]

  override def onNext(element: T): Unit = {
    ui.access {
      property.value = element
    }

    super.onNext(element)
  }
}