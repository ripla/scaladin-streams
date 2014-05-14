package vaadin.scala.streams

import vaadin.scala.server.ScaladinServlet
import javax.servlet.annotation.{WebInitParam, WebServlet}

@WebServlet(urlPatterns = Array("/*"), initParams = Array(new WebInitParam(name = "ScaladinUI", value ="vaadin.scala.streams.ScaladinStreamsUI")))
class ScaladinStreamsServlet extends ScaladinServlet