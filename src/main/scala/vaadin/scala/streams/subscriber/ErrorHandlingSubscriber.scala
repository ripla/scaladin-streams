package vaadin.scala.streams.subscriber

import com.vaadin.server.ErrorEvent

trait ErrorHandlingSubscriber[T] extends ScaladinSubscriber[T] {

  override def onError(cause: Throwable): Unit = ui.p.getErrorHandler.error(new ErrorEvent(cause))
}
