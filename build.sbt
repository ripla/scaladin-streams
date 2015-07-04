name := "Scaladin Streams"

scalaVersion := "2.11.7"

seq(vaadinWebSettings: _*)

resolvers += "Vaadin add-ons repository" at "http://maven.vaadin.com/vaadin-addons"

val vaadinVersion: String = "7.4.8"
val jettyVersion: String = "9.1.3.v20140225"
val akkaStreamsVersion = "1.0-RC4"

// basic dependencies
libraryDependencies ++= Seq(
  "com.vaadin" % "vaadin-server" % vaadinVersion,
  "com.vaadin" % "vaadin-client-compiled" % vaadinVersion,
  "com.vaadin" % "vaadin-themes" % vaadinVersion,
  "org.vaadin.addons" %% "scaladin" % "3.2-SNAPSHOT",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
  "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container",
  "org.eclipse.jetty" % "jetty-annotations" % jettyVersion % "container",
  "org.eclipse.jetty" % "jetty-continuation" % jettyVersion % "container"
)

libraryDependencies ++= Seq(
	//Add add-ons from the directory here. e.g.
	//"com.vaadin.addon" % "vaadin-charts" % "1.1.5"
)

//Stream related stuff
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaStreamsVersion,
  "com.vaadin" % "vaadin-push" % vaadinVersion,
  "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion % "container"
)

//Testing
libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

// Settings for the Vaadin plugin widgetset compilation
// Widgetset compilation needs memory and to avoid an out of memory error it usually needs more memory:
javaOptions in compileVaadinWidgetsets := Seq("-Xss8M", "-Xmx512M", "-XX:MaxPermSize=512M")

vaadinOptions in compileVaadinWidgetsets := Seq("-logLevel", "DEBUG", "-strict")

// Compile widgetsets into the source directory (by default themes are compiled into the target directory)
target in compileVaadinWidgetsets := (sourceDirectory in Compile).value / "webapp" / "VAADIN" / "widgetsets"

// This makes possible to attach a remote debugger when development mode is started from the command line
// javaOptions in vaadinDevMode ++= Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")