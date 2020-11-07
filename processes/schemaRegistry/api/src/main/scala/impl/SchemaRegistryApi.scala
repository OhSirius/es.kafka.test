package com.es.kafkatest.processes.schemaregistry.api.impl

import com.es.kafkatest.processes.schemaregistry.api.{ISchemaRegistryApi, ISchemaRegistryRepository}

import scala.io.Source
import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import scala.concurrent._
import scala.concurrent.duration._
import akka.http.scaladsl.model._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.http.scaladsl.unmarshalling.Unmarshal
import AkkaHttpImplicits.{executionContext, materializer, system}
import com.google.inject.Inject

//https://stackoverflow.com/questions/19045936/scalas-for-comprehension-with-futures
class SchemaRegistryApi @Inject() (private val registryRep: ISchemaRegistryRepository) extends ISchemaRegistryApi{
  import SchemaRegistryApi._

  //  # Регистрируем новую версию схемы для субъекта "Kafka-key" и "Kafka-value"
  //  $ curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  //  --data '{"schema": "{\"type\": \"string\"}"}' \
  //  http://localhost:8081/subjects/${subject}/versions
  //  {"id":1}
  //  # Обзор всех subjects
  //  $ curl -X GET http://localhost:8081/subjects
  //    ["Kafka-value","Kafka-key"]
  override def createInitSubjects(): Future[Boolean] = {
    val keyFuture = registryRep.createSubject(keySubject, simpleSchema);
    val valueFuture = registryRep.createSubject(valueSubject, simpleSchema);

    val res = for {result  <- keyFuture
                   result2 <- valueFuture
                   result3 <- registryRep.getSubjects}
      yield {
        println(s"Регистрируем новую версию схемы для субъекта ${keySubject} и ${valueSubject}")
        println("Ожидаем {\"id\":1}")
        println(s"Получаем ${result} и ${result2}\r\n")

        println("Обзор всех subjects")
        println("Ожидаем [\"Kafka-value\",\"Kafka-key\"]")
        println(s"Получаем ${result3}\r\n")
        true
      }

    res
  }

  //  # Получение схемы по глобальному id 141
  //  $ curl -X GET http://localhost:8081/schemas/ids/1
  //  {"schema":"\"string\""}
  //  # Получение схем всех версий для субъекта  "Kafka-key"
  //  $ curl -X GET http://localhost:8081/subjects/Kafka-key/versions
  //    [1]
  //  # Получение схемы версии 1 для субъекта "Kafka-key"
  //  $ curl -X GET http://localhost:8081/subjects/Kafka-key/versions/1
  //  {"subject":"Kafka-value","version":1,"id":1,"schema":"\"string\""}
  override def findScheme(): Future[Boolean] = {
    val schemaById = registryRep.getSchemaById(141)
    val schemaBySubject = registryRep.getSchemaBySubject(keySubject)
    val schemaByVersion = registryRep.getSchemaBySubjectAndVersion(keySubject, 1)
    for { res1 <- schemaById
          res2 <- schemaBySubject
          res3 <- schemaByVersion }
      yield {
        println("Получение схемы по глобальному id 1")
        println("Ожидаем {\"schema\":\"\\\"string\\\"\"}")
        println(s"Получаем ${res1}\r\n")

        println("Получение схем всех версий для субъекта  \"Kafka-key\"")
        println("Ожидаем [1]")
        println(s"Получаем ${res2}\r\n")

        println("Получение схемы версии 1 для субъекта \"Kafka-key\"")
        println("Ожидаем {\"subject\":\"Kafka-value\",\"version\":1,\"id\":1,\"schema\":\"\\\"string\\\"\"}")
        println(s"Получаем ${res3}\r\n")
        //return Future[Boolean] {true};
        true
      }
//    val res = Await.result(rr, 10 seconds)
//    return Future[Boolean] {true};
    //rr
  }

  //  # Удаляем схему версии 1 для субъекта "Kafka-key"
  //  $ curl -X DELETE http://localhost:8081/subjects/Kafka-key/versions/1
  //  1
  override def deleteAndRegisterScheme(): Future[Boolean] = {
    for {res1 <- registryRep.deleteSchemaBySubjectAndVersion(keySubject, 1)
         res2 <- registryRep.createSubject(keySubject, simpleSchema)
         res3 <- registryRep.getSchemaById(1)    }
      yield {
        println("Удаление зарегистрированной схемы для субъекта \"Kafka-key\"")
        println("Ожидаем 2")
        println(s"Получаем ${res1}\r\n")

        println(s"Регистрируем новую версию схему (повторно) для субъекта ${keySubject}")
        println("Ожидаем {\"id\":1}")
        println(s"Получаем ${res2}\r\n")

        println("Получение схемы по глобальному id 1")
        println("Ожидаем {\"schema\":\"\\\"string\\\"\"}")
        println(s"Получаем ${res3}\r\n")
        true
      }
  }

  //  # Тестируем совместимость схемы с последней версией схемы в субъекте "Kafka-key"
  //  $ curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  //  --data '{"schema": "{\"type\": \"string\"}"}' \
  //  http://localhost:8081/compatibility/subjects/Kafka-value/versions/latest
  //  {"is_compatible":true}
  //  # Обновляем требования совместимости глобально
  //    $ curl -X PUT -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  //  --data '{"compatibility": "NONE"}' \
  //  http://localhost:8081/config
  //    {"compatibility":"NONE"}
  //  # Обновляем требования совместимости для субъекта "Kafka-value"
  //  $ curl -X PUT -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  //  --data '{"compatibility": "BACKWARD"}' \
  //  http://localhost:8081/config/Kafka-value
  //  {"compatibility":"BACKWARD"}
  override def checkAndChangeCompatibilityScheme(): Future[Boolean] = {
    for {res1 <- registryRep.checkCompatibilitySchema(keySubject, simpleSchema)
         res2 <- registryRep.updateCompatibilityLevel(noneCompatibilityConfig)
         res3 <- registryRep.updateCompatibilityLevelForSubject(valueSubject, backCompatibilityConfig) }
      yield {
        println("Тестируем совместимость схемы с последней версией схемы в субъекте \"Kafka-key\"")
        println("Ожидаем {\"is_compatible\":true}")
        println(s"Получаем ${res1}\r\n")

        println("Обновляем требования совместимости глобально")
        println("Ожидаем {\"compatibility\":\"NONE\"}")
        println(s"Получаем ${res2}\r\n")

        println("Обновляем требования совместимости для субъекта \"Kafka-value\"")
        println("Ожидаем {\"compatibility\":\"BACKWARD\"}")
        println(s"Получаем ${res3}\r\n")
        true
      }
  }

  //  # Удаляем все версии для субъекта "Kafka-value"
  //  $ curl -X DELETE http://localhost:8081/subjects/Kafka-value
  //    [3]
  override def deleteAllScheme(): Future[Boolean] = {
    for { res1 <- registryRep.deleteSchemaBySubject(valueSubject)
          res2 <- registryRep.getSubjects()    }
      yield {
        println("Удаляем все версии для субъекта \"Kafka-value\"")
        println("EXPECTING [3]")
        println(s"Получаем ${res1}\r\n")

        println("Список всех субъектов")
        println("Ожидаем [\"Kafka-key\"]")
        println(s"Получаем ${res2}\r\n")
        true
      }

  }

}

object SchemaRegistryApi{
  private val keySubject: String = "Kafka-key"
  private val valueSubject = "Kafka-value"
  private lazy val simpleSchema = Source.fromURL(getClass.getResource("/simpleStringSchema.avsc")).mkString
  private lazy val noneCompatibilityConfig = Source.fromURL(getClass.getResource("/compatibilityNONE.json")).mkString
  private lazy val backCompatibilityConfig = Source.fromURL(getClass.getResource("/compatibilityBACKWARD.json")).mkString
}
