name := "data-pipeline"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= Seq("com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "2.0.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "2.0.0",
  "com.typesafe.akka" %% "akka-stream" % "2.6.5",

  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % "2.6.5",
  "com.typesafe.play" %% "play-json" % "2.8.1"
)