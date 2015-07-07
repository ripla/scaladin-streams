package vaadin.scala.streams

import akka.stream.scaladsl.Sink
import vaadin.scala.streams.generators.UserStory

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Very simple tests to see that files can be read etc.
 */
class StoryGeneratorTest extends ScaladinStreamTest {

  behavior of "Story generator"

  it should "generate random stories" in {
    val story = UserStory.getStory()

    assert(story != null)
    assert(!story.isEmpty)
    Console.println(story)
  }

  it should "generate random stories as a stream" in {
    val stories = UserStory.getStories()

    val storiesFuture: Future[Seq[String]] = stories.grouped(3).runWith(Sink.head)

    val result = Await.result(storiesFuture, 100 millis)

    assert(result != null)
    assert(!result.isEmpty)
    Console.println(result)
  }
}
