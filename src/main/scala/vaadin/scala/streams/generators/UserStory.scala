package vaadin.scala.streams.generators

/**
 * Created by risto on 4.7.2015.
 */
object UserStory {


  def createStory(who: String, what: String, why: String) = s"As a $who, I want to$what, so that I can $why"


}
