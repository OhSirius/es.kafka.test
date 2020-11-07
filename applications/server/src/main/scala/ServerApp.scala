package com.es.kafkatest.app.server

import java.util.UUID

import com.typesafe.scalalogging.Logger
import org.rogach.scallop.ScallopConf
import org.slf4j.LoggerFactory

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Random
import scala.reflect._


object ServerApp extends App {
  private class Args(args: Array[String]) extends ScallopConf(args) {
    val process = opt[String](required = true, default = Some("schemaRegistry"))//,
    verify()
  }

  private lazy val logger = Logger(LoggerFactory.getLogger(this.getClass))

  override def main(args:Array[String]):Unit = {
    try {
      val conf = new Args(args)
      //Название процесса
      val processName = conf.process.toOption.get

      //Инициализируем настройки
      val settings = new Settings()
      settings.initialize()

      logger.info(s"Запускаем процесс ${processName}...")
      //Запускаем процессы
      processName match {
        case "genericAvro" => {
          import com.es.kafkatest.processes.genericavro.consumer.bootstrap.ConsumerConfiguration
          import com.es.kafkatest.processes.genericavro.consumer.impl.ConsumerProps
          import com.es.kafkatest.processes.genericavro.models.User

          val conFactory = new ConsumerConfiguration[User]().configure()
          conFactory.create(new ConsumerProps(settings.avroGenericProcessTopic, settings.avroGenericGroup, settings.kafkaServers,
            settings.avroGenericAutoCommit, settings.avroGenericCommitInterval, settings.avroGenericSessionTimeout,
            settings.avroGenericConsumerTimeout, settings.avroGenericConsumerPollTimeout)).start()
        }
        case "schemaRegistry" => {
          import com.es.kafkatest.processes.schemaregistry.consumer.bootstrap.ConsumerConfiguration
          import com.es.kafkatest.processes.schemaregistry.consumer.impl.ConsumerProps
          import com.sul.distributedRepository.models.IdentityUser
          import com.es.kafkatest.processes.schemaregistry.models.{User}//, IdentityUser}
          val conFactory = new ConsumerConfiguration[IdentityUser]().configure()
          conFactory.create(new ConsumerProps(settings.schemaRegistryProcessTopic, settings.schemaRegistryGroup, settings.kafkaServers,
            settings.schemaRegistryAutoCommit, settings.schemaRegistryCommitInterval, settings.schemaRegistrySessionTimeout,
            settings.schemaRegistryConsumerTimeout, settings.schemaRegistryConsumerPollTimeout,settings.schemaRegistry)).start()
        }
        case "schemaRegistryApi" =>{
          import com.es.kafkatest.processes.schemaregistry.api.bootstrap.ApiConfiguration
          import com.es.kafkatest.processes.schemaregistry.api.impl.Props
          import com.es.kafkatest.processes.schemaregistry.api.impl.AkkaHttpImplicits.{executionContext, materializer, system}
          val api = new ApiConfiguration(Props(settings.schemaRegistry)).configure()
          Await.result(api.createInitSubjects(), 10 seconds)
          Await.result(api.findScheme(), 10 seconds)
          Await.result(api.deleteAndRegisterScheme(), 10 second)
          Await.result(api.checkAndChangeCompatibilityScheme(), 10 second)
          Await.result(api.deleteAllScheme(), 10 second)
        }
        case "streamsConverter" =>{
          import com.es.kafkatest.processes.streams.converters.bootstrap.StreamsConverterConfiguration
          import com.es.kafkatest.processes.schemaregistry.models.UserWithBooleanId
          import com.es.kafkatest.processes.schemaregistry.models.User
          import com.es.kafkatest.processes.streams.converters.impl.StreamsProps
          import com.es.kafkatest.processes.schemaregistry.models.SinkUser
          import scala.reflect._
          import scala.reflect.runtime.universe._

          var factory = new StreamsConverterConfiguration[User, SinkUser]().configure()
          factory.create(new StreamsProps(settings.configStreamsTopic, settings.kafkaServers, settings.schemaRegistry)).start(settings.inputStreamsTopic,settings.outputStreamsTopic)
        }
        case "streamsConverterResult" => {
          import com.es.kafkatest.processes.schemaregistry.consumer.bootstrap.ConsumerConfiguration
          import com.es.kafkatest.processes.schemaregistry.consumer.impl.ConsumerProps
          import com.es.kafkatest.processes.schemaregistry.models.UserWithBooleanId
          val conFactory = new ConsumerConfiguration[UserWithBooleanId]().configure()
          conFactory.create(new ConsumerProps(settings.outputStreamsTopic, settings.schemaRegistryGroup, settings.kafkaServers,
            settings.schemaRegistryAutoCommit, settings.schemaRegistryCommitInterval, settings.schemaRegistrySessionTimeout,
            settings.schemaRegistryConsumerTimeout, settings.schemaRegistryConsumerPollTimeout,settings.schemaRegistry)).start()
        }
        case "redisQueue" => {

          import com.es.kafkatest.processes.redisconsumer.bootstrap._

          val props = Props(settings.redisServer, settings.redisPort,settings.redisThemeQueue)
          val process = new RedisConsumerConfiguration(props).configure()
          process.start()
        }
        case _ => logger.error(s"Не известный процесс $processName")
      }
    }
    catch {
      case ex: Exception =>
        //println(ex.printStackTrace().toString)
        //ex.printStackTrace()
      logger.error(ex.printStackTrace().toString)
    }

  }

}
