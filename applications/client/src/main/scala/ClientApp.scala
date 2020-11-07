package com.es.kafkatest.app.client

import java.util.UUID

import com.easysales.kafkatest.databases.common.RepTypes
import com.es.kafkatest.processes.schemaregistry.models
import com.es.kafkatest.processes.schemaregistry.models._

import scala.concurrent.Await
import scala.reflect.internal.Trees
import scala.util.Try

//import com.es.kafkatest.processes.genericavro.{models => ga}
//import com.es.kafkatest.processes.genericavro.producer.bootstrap.ProducerConfiguration
//import com.es.kafkatest.processes.genericavro.producer.impl.ProducerProps

import com.es.kafkatest.inf.common.extensions.StringExtensions._
import org.rogach.scallop.ScallopConf
//import com.es.kafkatest.schemaregistry.{models  => sr}
import scala.util.Random
import com.es.kafkatest.inf.common.extensions.ResourcesExtensions._
import scala.concurrent.duration._
import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory
import scala.reflect.macros.Context
import  scala.collection.immutable._

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.typesafe.config.Config
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory.{getILoggerFactory, getLogger}


//https://alvinalexander.com/scala/how-to-use-scala-match-expression-like-switch-case-statement
object ClientApp extends  App {
  private class Args(args: Array[String]) extends ScallopConf(args) {
    val process = opt[String](required = true, default = Some("schemaRegistry"))//,
    verify()
  }

  private lazy val logger = Logger(LoggerFactory.getLogger(this.getClass))

  override def main(args: Array[String]): Unit = {
    try {
      val conf = new Args(args)

      //Название процесса
      val processName = conf.process.toOption.get

      //Инициализируем настройки
      val settings = new Settings()
      settings.initialize()

      //println(s"Запускаем процесс ${processName}..., ${logger.underlying.isInfoEnabled}, ${logger.underlying.getName}, ${logger.underlying}, ${logger}")
      logger.info(s"Запускаем процесс ${processName}...")
      //logger.underlying.error(s"Запускаем процесс ${processName}..., ${logger.underlying.getName}")
      //val rootLogger= getLogger(ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]
      //rootLogger.info(s"test, ${rootLogger.getLevel}, ${rootLogger.getName}, ${rootLogger.getLoggerContext}, ${rootLogger.getAppender("FILE")}")
      //val loggerContext = getILoggerFactory.asInstanceOf[LoggerContext]

      //for(val ss <- rootLogger.iteratorForAppenders()){

      //}


      //logger.underlying.debug(s"Запускаем процесс ${processName}...")
      //Запускаем процессы
      processName match {
        case "genericAvro" => {
          import com.es.kafkatest.processes.genericavro.models.User
          import com.es.kafkatest.processes.genericavro.producer.bootstrap.ProducerConfiguration
          import com.es.kafkatest.processes.genericavro.producer.impl.ProducerProps


          val user = User(Random.nextInt(), "sirius", Some("aspavlychev@yandex.ru"))
          val prodFactory = new ProducerConfiguration[User]().configure()
          usingResource(prodFactory.create(new ProducerProps(settings.avroGenericProcessTopic, settings.kafkaServers, UUID.randomUUID())))(p=>p.send(user))
          //producer.send(user)
          //producer.close
          //scala.io.StdIn.readLine
          logger.info(s"Завершаем процесс ${processName}...")
        }
        case "schemaRegistry" =>{
          import com.es.kafkatest.processes.schemaregistry.models._//User
          import com.es.kafkatest.processes.schemaregistry.producer.bootstrap.ProducerConfiguration
          import com.es.kafkatest.processes.schemaregistry.producer.impl.ProducerProps
          import com.sul.distributedRepository.models.IdentityUser

          //val user = User(Random.nextInt(), "sirius")
          val user = IdentityUser(Random.nextInt(), "sirius", "sdsd", "sdfsdf", "sdfsdf", 9)
          val prodFactory = new ProducerConfiguration[IdentityUser]().configure()
          usingResource(prodFactory.create(new ProducerProps(settings.schemaRegistryTopic, settings.kafkaServers, UUID.randomUUID(),settings.schemaRegistry)))(p=>p.send(user))
        }
        case "checkSchemaRegistry" =>{
          import com.es.kafkatest.processes.schemaregistry.models._//User
          import com.es.kafkatest.processes.schemaregistry.producer.bootstrap.ProducerConfiguration
          import com.es.kafkatest.processes.schemaregistry.producer.impl.ProducerProps

          logger.info("1. Отправляем событие, соотвествующее схеме данного topic")
          val user = User(Random.nextInt(), "sirius")
          val prodFactory = new ProducerConfiguration[User]().configure()
          usingResource(prodFactory.create(new ProducerProps(settings.schemaRegistryTopic, settings.kafkaServers, UUID.randomUUID(),settings.schemaRegistry)))(p=>p.send(user))

          logger.info("2. Отправляем событие, соотвествующее схеме данного topic (без имени!)")
          val user2 = UserWithoutName(Random.nextInt())
          val prodFactory2 = new ProducerConfiguration[UserWithoutName]().configure()
          usingResource(prodFactory2.create(new ProducerProps(settings.schemaRegistryTopic, settings.kafkaServers, UUID.randomUUID(),settings.schemaRegistry)))(p=>p.send(user2))

          logger.info("3. Отправляем событие, НЕ соотвествующее схеме данного topic (другой тип Id!)")
          val user3 = UserWithBooleanId(true)
          val prodFactory3 = new ProducerConfiguration[UserWithBooleanId]().configure()
          usingResource(prodFactory3.create(new ProducerProps(settings.schemaRegistryTopic, settings.kafkaServers, UUID.randomUUID(),settings.schemaRegistry)))(p=>p.send(user3))

          logger.info("4. Отправляем событие, НЕ соотвествующее схеме данного topic (новый тип!)")
          val user4 = UserNew("user1")
          val prodFactory4 = new ProducerConfiguration[UserNew]().configure()
          usingResource(prodFactory4.create(new ProducerProps(settings.schemaRegistryTopic, settings.kafkaServers, UUID.randomUUID(),settings.schemaRegistry)))(p=>p.send(user4))

        }
        case "streamsConverter" =>{
          import com.es.kafkatest.processes.schemaregistry.models._//User
          import com.es.kafkatest.processes.schemaregistry.producer.bootstrap.ProducerConfiguration
          import com.es.kafkatest.processes.schemaregistry.producer.impl.ProducerProps

          logger.info("1. Отправляем событие, соотвествующее схеме данного topic")
          val user = User(Random.nextInt(), "sirius")
          val prodFactory = new ProducerConfiguration[User]().configure()
          usingResource(prodFactory.create(new ProducerProps(settings.inputStreamsTopic, settings.kafkaServers, UUID.randomUUID(),settings.schemaRegistry)))(p=>p.send(user))

        }
        case "sqlServer" =>{
          import com.es.kafkatest.processes.database.sqlServer.bootstrap.{SqlServerConfiguration}
          import com.easysales.kafkatest.databases.common.Props
          import slick.basic.DatabaseConfig
          import slick.jdbc.SQLServerProfile

          logger.info("Соединение с SqlServer и таблицей LinuxUser и LinuxRole в режиме ORM (Slick)")
          var process = new SqlServerConfiguration(Props(settings.sqlServerConfigPath, settings.config), RepTypes.Slick).configure()
          Await.result(process.findUsers(), 50 seconds)
          Await.result(process.createUsers(), 50 seconds)
          Await.result(process.deleteUsers(), 50 seconds)

          logger.info("Соединение с SqlServer и таблицей LinuxUser и LinuxRole в режиме инъекций (DirectSql)")
          process = new SqlServerConfiguration(Props(settings.sqlServerConfigPath, settings.config), RepTypes.DirectSql).configure()
          Await.result(process.findUsers(), 50 seconds)
          Await.result(process.createUsers(), 50 seconds)
          Await.result(process.deleteUsers(), 50 seconds)
        }
        case "PostgreSql" =>{
          import com.es.kafkatest.processes.database.postgreSql.bootstrap.PosgreSqlConfiguration
          import com.easysales.kafkatest.databases.common.Props
          import slick.basic.DatabaseConfig
          import slick.jdbc.SQLServerProfile

          logger.info("Соединение с PostgreSql и таблицей PostgreUser и Role в режиме ORM (Slick)")
          var process = new PosgreSqlConfiguration(Props(settings.PostgreSqlConfigPath, settings.config), RepTypes.Slick).configure()
          Await.result(process.findUsers(), 50 seconds)
          Await.result(process.createUsers(), 50 seconds)
          Await.result(process.deleteUsers(), 50 seconds)


          logger.info("Соединение с PostgreSql и таблицей PostgreUser и Role в режиме инъекций (DirectSql)")
          process = new PosgreSqlConfiguration(Props(settings.PostgreSqlConfigPath, settings.config), RepTypes.DirectSql).configure()
          Await.result(process.findUsers(), 50 seconds)
          Await.result(process.createUsers(), 50 seconds)
          Await.result(process.deleteUsers(), 50 seconds)

        }
        //case _ => logger.info(s"Не известный процесс $processName")
        case "redis" => {
          import com.es.kafkatest.processes.redis.bootstrap._

          val process = new RedisConfiguration(Props(settings.redisServer, settings.redisPort )).configure()

          var listUser : List[User] = List(new User(Random.nextInt(), "name1") ,
                                          new User(Random.nextInt(), "name2"),
                                           new User(Random.nextInt(), "name3"))
          println(listUser)
          //отправляем пользователей в redis. Ключи - id пользователей
          listUser.foreach(u =>
            {
              Await.result(process.put(u), 50 seconds)
            })

          //извлекаем пользователей из redis по ключам. Ключи - id пользователей
          val r = Await.result(process.pull(listUser.last.id), 50 seconds)



        }
        case "redisQueue" => {
          import com.es.kafkatest.processes.redis.redisqueue.bootstrap._

          var process = new RedisConfiguration(QueueProps(settings.redisServer, settings.redisPort, settings.redisThemeQueue)).configure()

          var listUser : List[User] = List(new User(Random.nextInt(), "name1") ,
                                            new User(Random.nextInt(), "name2"),
                                            new User(Random.nextInt(), "name3"))
          //отправляем пользователей в redis. Ключи - id пользователей
          listUser.foreach(u =>
          {
            process.send(u)
            println(s"Отправили пользователя ${u.id} ${u.name}")
            logger.info(s"Отправили пользователя ${u.id} ${u.name}")
          })
        }
        case _ => println(s"Не известный процесс $processName")

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
