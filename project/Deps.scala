import sbt._

object Deps {
  //lazy val kafka = "org.apache.kafka" % "kafka_2.11" % "1.1.0"
  lazy val kafka = "org.apache.kafka" % "kafka_2.12" % "2.1.0"
  lazy val avro =  "org.apache.avro" % "avro" % "1.8.2" exclude("org.slf4j","*")
  lazy val avroSerializer = "io.confluent" % "kafka-avro-serializer" % "3.2.1" exclude("org.slf4j","*")
  lazy val logBack = "ch.qos.logback" %  "logback-classic" % "1.2.3"
  lazy val logging = "com.typesafe.scala-logging" % "scala-logging_2.12" % "3.9.0"
  lazy val guiceDI = "com.google.inject" % "guice" % "4.2.0"
  lazy val guiceExDI = "com.google.inject.extensions" % "guice-assistedinject" % "4.2.0"
  lazy val scalaGuice = "net.codingwell" %% "scala-guice" % "4.2.1"
  lazy val typesafe = "com.typesafe" % "config" % "0.4.0"
  lazy val scalaCompiler = "org.scala-lang" % "scala-reflect" % "2.12.6"
  lazy val scallop = "org.rogach" %% "scallop" % "3.1.1"

  lazy val httpAkka = "com.typesafe.akka" %%  "akka-http"             % "10.1.4"
  lazy val jsonAkka = "com.typesafe.akka" %%  "akka-http-spray-json"  % "10.1.4"
  lazy val spray = "io.spray"          %%  "spray-json"            % "1.3.4"
  lazy val actorAkka = "com.typesafe.akka" %%  "akka-actor"            % "2.5.15"
  lazy val streamAkka =  "com.typesafe.akka" %%  "akka-stream"           % "2.5.15"
  lazy val scalaTest = "org.scalatest"     %%  "scalatest"             % "3.0.5"     % Test

  lazy val kafkaClients = "org.apache.kafka" % "kafka-clients" % "1.1.0"
  lazy val kafkaStreams = "org.apache.kafka" % "kafka-streams" % "1.1.0"
  lazy val kafkaStreamsAvroSerializer = "io.confluent" % "kafka-streams-avro-serde" % "4.1.0"

  lazy val sql = "com.microsoft.sqlserver" % "mssql-jdbc" % "6.2.1.jre8"//"7.0.0.jre10" % Test//"net.sourceforge.jtds"      %     "jtds" % "1.3.1"
  lazy val slick =   "com.typesafe.slick" %% "slick" % "3.2.3"
  lazy val exslick =  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3" //"com.typesafe.slick" %% "slick-extensions" % "3.0.0"
  lazy val jtds = "net.sourceforge.jtds" % "jtds" % "1.3.1"
  lazy val redisClient = "net.debasishg" %% "redisclient" % "3.8"

  lazy val postgre = "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
  lazy val hikari = "com.zaxxer" % "HikariCP-java6" % "2.3.2"
  lazy val zstd = "com.github.luben" % "zstd-jni" % "VERSION"
 }