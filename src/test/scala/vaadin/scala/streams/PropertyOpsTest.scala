package vaadin.scala.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl2.{Source, FlowMaterializer}
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import vaadin.scala._

class PropertyOpsTest extends FlatSpec with MockitoSugar {

  behavior of "Property implicits"

  it should "allow values to be produced to a property implicitly" in {
    implicit val ui = mock[UI]
    implicit val system = ActorSystem("Test")

    Source.apply()
    val materializer = FlowMaterializer()
    val producer = Flow(() => 1).toProducer(materializer)
    val property: Property[Int] = Property(1)
    producer.produceTo(property)

    assert(property.value === Some(1))
  }


  it should "allow values to be produced from a property implicitly" in {
    implicit val ui = mock[UI]
    implicit val system = ActorSystem("Test")
    val session = mock[ScaladinSession]

    val mockConsumer: Consumer[Int] = mock[Consumer[Int]]
    val mockSubscriber: Subscriber[Int] = mock[Subscriber[Int]]
    val property: Property[Int] with ValueChangeNotifier = new ObjectProperty[Int](1) with ValueChangeNotifier

    Mockito.when(mockConsumer.getSubscriber).thenReturn(mockSubscriber)
    Mockito.when(ui.session).thenReturn(session)
    Mockito.when(session.lockInstance).thenReturn(new ReentrantLock())
    property.produceTo(mockConsumer)

    val subscriptionCaptor: ArgumentCaptor[Subscription] = ArgumentCaptor.forClass(classOf[Subscription])
    Mockito.verify(mockSubscriber).onSubscribe(subscriptionCaptor.capture())
    subscriptionCaptor.getValue.requestMore(1)
    property.value = 2
    Mockito.verify(mockSubscriber).onNext(2)
  }
}
