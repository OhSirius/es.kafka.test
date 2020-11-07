//name := "EasySalesKafkaAvroTest"

//version := "0.1"

//scalaVersion := "2.12.6"
//import sbtavrohugger._
import Deps._

//watchSources ++= ((sourceDirectory in Compile).value ** "*.avsc").get

//avroScalaSpecificCustomNamespace in Compile := Map("com.es.kafkatest.schemaregistry.users_10"->"overridden")
//avroSpecificSourceDirectories in Compile += (sourceDirectory in Compile).value / "processes/schemaRegistry/models/src/main/resources"
//sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue


lazy val root = (project in file(".")).
  aggregate(client, server).
  settings(
    inThisBuild(List(
      organization := "com.easysales",
      scalaVersion := "2.12.6",
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      resolvers += "io.confluent" at "http://packages.confluent.io/maven/"
    )),
    name := "com.easysales.kafkatest",
    //specificAvroSettings+=
    //sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,
  )
  .enablePlugins(AssemblyPlugin)
  //.specificAvroSettings( sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,)

//Клиент и сервер
lazy val client = (project in file ("applications/client")).
  settings(
    name := "com.easysales.kafkatest.app.client",
    mainClass := Some("com.es.kafkatest.app.client.ClientApp"),
    scalaVersion:="2.12.6",
    libraryDependencies ++= Seq(
      typesafe,
      scallop,
      logBack,
      logging
    ),
    //javaOptions in run ++= Seq("/resources/logback-test.xml"),
    test in assembly := {},
    assemblyJarName in assembly := s"es-kafkatest-app-client.jar",
    assemblyMergeStrategy in assembly := {
      //case PathList("logback.xml") => MergeStrategy.discard
      case PathList("application.conf") => MergeStrategy.concat
      case PathList("reference.conf") => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )
  .enablePlugins(AssemblyPlugin)
  .dependsOn(common, avroModels, avroProducer, schemaRegistryModels, schemaRegistryProducer, sqlDatabase, redis, postgreDatabase)

lazy val server = (project in file ("applications/server")).
  settings(
    name := "com.easysales.kafkatest.app.server",
    mainClass := Some("com.es.kafkatest.app.server.ServerApp"),
    libraryDependencies ++= Seq(
      typesafe,
      scallop,
      logBack,
      logging
    ),
    test in assembly := {},
    assemblyJarName in assembly := s"es-kafkatest-app-server.jar",
    assemblyMergeStrategy in assembly := {
      //case PathList("app.conf") => MergeStrategy.concat
      case PathList("application.conf") => MergeStrategy.concat
      case PathList("reference.conf") => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
    //logLevel in assembly := Level.Debug
  )
  .dependsOn(common, avroModels, avroConsumer, schemaRegistryModels, schemaRegistryConsumer, schemaRegistryApi, convertStream, redisConsumer)
  .enablePlugins(AssemblyPlugin)

lazy val websocketServer = (project in file ("applications/websocketServer")).
  settings(
    name := "com.easysales.kafkatest.app.websocketServer",
    mainClass := Some("com.es.kafkatest.app.websocketserver.WebSocketServerApp"),
    scalaVersion:="2.12.6",
    test in assembly := {},
    assemblyJarName in assembly := s"es-kafkatest-app-websocketserver.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("application.conf") => MergeStrategy.concat
      case PathList("reference.conf") => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case _ => MergeStrategy.first
    },
    libraryDependencies ++= Seq(
      httpAkka,
      jsonAkka,
      spray,
      actorAkka,
      streamAkka,
      typesafe,
      scallop)
  )
  .dependsOn(common)
  .enablePlugins(AssemblyPlugin)


//Инфраструктура
lazy val common = (project in file ("infrastructure/common")).
  settings(
    name := "com.easysales.kafkatest.inf.common",
    libraryDependencies ++= Seq(
      avro,
      scalaCompiler,
      guiceDI,
      guiceExDI,
      scalaGuice)
  )

lazy val commonDb = (project in file("processes/databases/common")).
  settings(
    name := "com.easysales.kafkatest.databases.common",
    libraryDependencies ++= Seq(
      typesafe
    )
  )

//GenericAVRO - тестирование Kafka с использованием GenericRecord
lazy val avroModels = (project in file ("processes/genericAvro/models")).
  settings(
    name := "com.easysales.kafkatest.genericavro.models.scheme",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack)
  ).dependsOn(common)


lazy val avroProducer = (project in file ("processes/genericAvro/producer")).
  settings(
    name := "com.easysales.kafkatest.genericavro.core.producer",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack,
      guiceDI,
      guiceExDI,
      scalaGuice,
      scalaCompiler,
      zstd
      )
  ).dependsOn(common, avroModels)

lazy val avroConsumer = (project in file ("processes/genericAvro/consumer")).
  settings(
    name := "com.easysales.kafkatest.genericavro.core.consumer",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack,
      guiceDI,
      guiceExDI,
      scalaGuice,
      scalaCompiler,
      zstd
      )
  ).dependsOn(common, avroModels)

//AVRO - тестирование Kafka с использованием Schema Registry
//https://gitter.im/julianpeeters/avrohugger/archives/2018/06/20
//https://gitter.im/julianpeeters/avrohugger/archives/2016/05/21
//https://andyboyle.io/2017/04/29/apache-avro-for-serialized-messaging-using-scala-and-sbt/
//sbtavrohugger.SbtAvrohugger.specificAvroSettings += avroSpecificScalaSource in Compile := new java.io.File("MySource")
//https://github.com/julianpeeters/sbt-avrohugger/issues/39
//https://stackoverflow.com/questions/47756007/sbt-not-generating-avro-classes
lazy val schemaRegistryModels = (project in file ("processes/schemaRegistry/models")).
  //settings(sbtavrohugger.SbtAvrohugger.specificAvroSettings:_*).
  settings(
    name := "com.easysales.kafkatest.schemaregistry.models.scheme",
    avroSpecificSourceDirectories in Compile += (sourceDirectory in Compile).value / "resources",//{_/ "src/main/resources"}.value,//  "src/main/resources",//(sourceDirectory in Compile).value
    //(avroSpecificSourceDirectories in Compile) += new java.io.File("src/main/resources/com/es/kafkatest/schemaregistry/users_5"),
    //(avroSpecificSourceDirectories in Compile) += new java.io.File("src/main/resources"),
    //avroSpecificScalaSource in Compile := new java.io.File( "/MySource"),
    avroSpecificScalaSource in Compile := (sourceDirectory in Compile).value / "scala",
    //avroScalaSpecificCustomNamespace in Compile := Map("myavro"->"overridden", "overridden"->"myavro"),
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack),
      //specificAvroSettings+=avroSpecificScalaSource in Compile := new java.io.File("MySource"),

      //

    //avroSpecificSourceDirectories in Compile += new java.io.File("src/main/resources/com.easysales.kafkatest.schemaregistry.models.scheme"),
    //avroSpecificSourceDirectories in Compile += (sourceDirectory in Compile).value / "src/main/resources/",



  ).dependsOn(common)

lazy val schemaRegistryProducer = (project in file ("processes/schemaRegistry/producer")).
  settings(
    name := "com.easysales.kafkatest.schemaregistry.core.producer",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack,
      guiceDI,
      guiceExDI,
      scalaGuice,
      scalaCompiler

    )
  ).dependsOn(common, schemaRegistryModels)

lazy val schemaRegistryConsumer = (project in file ("processes/schemaRegistry/consumer")).
  settings(
    name := "com.easysales.kafkatest.schemaregistry.core.consumer",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack,
      guiceDI,
      guiceExDI,
      scalaGuice,
      scalaCompiler
    )
  ).dependsOn(common, schemaRegistryModels)

//sourceGenerators in Compile += (avroScalaGenerate in Compile).taskValue
//avroSpecificSourceDirectories in Compile += (sourceDirectory in Compile).value / "./processes/schemaRegistry/models/src/main/resources"
//avroSpecificScalaSource in Compile := new java.io.File("./processes/schemaRegistry/models/src/main/myScalaSource")

lazy val schemaRegistryApi = (project in file ("processes/schemaRegistry/api")).
  settings(
    name := "com.easysales.kafkatest.schemaregistry.core.api",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack,
      guiceDI,
      guiceExDI,
      scalaGuice,
      scalaCompiler,
      httpAkka,
      jsonAkka,
      spray,
      actorAkka,
      streamAkka,
      scalaTest
    )
  ).dependsOn(common)

lazy val convertStream = (project in file ("processes/streams/converter")).
settings(
  name := "com.easysales.kafkatest.streams.core.converter",
  libraryDependencies ++= Seq(
    kafka,
    avro,
    avroSerializer,
    logBack,
    guiceDI,
    guiceExDI,
    scalaGuice,
    scalaCompiler,
    kafkaClients,
    kafkaStreams,
    kafkaStreamsAvroSerializer
  )
).dependsOn(common, schemaRegistryModels)

lazy val stateStoreStream = (project in file ("processes/streams/stateStore")).
  settings(
    name := "com.easysales.kafkatest.streams.core.stateStore",
    libraryDependencies ++= Seq(
      kafka,
      avro,
      avroSerializer,
      logBack,
      guiceDI,
      guiceExDI,
      scalaGuice,
      scalaCompiler,
      kafkaClients,
      kafkaStreams,
      kafkaStreamsAvroSerializer
    )
  ).dependsOn(common, schemaRegistryModels)

lazy val sqlDatabase = (project in file ("processes/databases/sqlServer")).
  settings(
    name:="com.easysales.kafkatest.databases.core.sqlServer",
    libraryDependencies ++= Seq(
      sql,
      slick,
      exslick,
      guiceDI,
      guiceExDI,
      scalaGuice,
      jtds
    ),
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
  ).dependsOn(common,commonDb)


lazy val postgreDatabase = (project in file ("processes/databases/postgreSql")).
  settings(
    name:="com.easysales.kafkatest.databases.core.postgreSql",
    libraryDependencies ++= Seq(
      postgre,
      hikari,
      slick,
      exslick,
      guiceDI,
      guiceExDI,
      scalaGuice,
      jtds
    ),
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
  ).dependsOn(common,commonDb)

//enablePlugins(JavaAppPackaging)
//enablePlugins(DockerPlugin)
//enablePlugins(AshScriptPlugin)
lazy val redis = (project in file ("processes/redis/redis"))
  .settings(
    name:= "com.easysales.kafkatest.redis",
    libraryDependencies ++= Seq(
      redisClient,
      guiceDI,
      guiceExDI,
      scalaGuice,
      typesafe
    )).dependsOn(common, schemaRegistryModels)

lazy val redisConsumer = (project in file("processes/redis/redisConsumer"))
.settings(
  name:= "com.easysales.kafkatest.redisConsumer",
  libraryDependencies ++= Seq(
    redisClient,
    guiceDI,
    guiceExDI,
    scalaGuice,
    typesafe
  )).dependsOn(common, schemaRegistryModels)
