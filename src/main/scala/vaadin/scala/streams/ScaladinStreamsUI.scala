package vaadin.scala.streams

import vaadin.scala._

class ScaladinStreamsUI extends UI(title = "Scaladin Streams") {
	
	content = new VerticalLayout {
		margin = true
		components += Label("This Vaadin app uses Scaladin!")
	}
}