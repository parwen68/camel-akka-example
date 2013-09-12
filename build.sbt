
organization := "Callista"

name := "Camel-Akka"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.2"
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies := Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "com.typesafe.akka" %% "akka-camel" % "2.2.1",
  "org.apache.camel" % "camel-jetty" % "2.11.1"
  )
