package com.es.kafkatest.processes.schemaregistry.api.impl

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
import com.es.kafkatest.processes.schemaregistry.api.ISchemaRegistryRepository
import AkkaHttpImplicits.{executionContext, materializer, system}
import com.google.inject.Inject

case class Props(schemaRegistryUrl:String)//, timeout:Duration)

class SchemaRegistryRepository @Inject() (private val props:Props ) extends ISchemaRegistryRepository {
  import SchemaRegistryRepository._

  def createSubject(name: String, payload:String): Future[String] = {
    post(payload,s"${props.schemaRegistryUrl}/subjects/${name}/versions")
  }

  def getSubjects(): Future[String] = {
    get(s"${props.schemaRegistryUrl}/subjects")
  }

  override def getSchemaById(id: Int): Future[String] = {
    get(s"${props.schemaRegistryUrl}/schemas/ids/${id}")
  }

  override def getSchemaBySubject(subject: String): Future[String] = {
    get(s"${props.schemaRegistryUrl}/subjects/${subject}/versions")
  }

  override def getSchemaBySubjectAndVersion(subject: String, version: Int): Future[String] = {
    get(s"${props.schemaRegistryUrl}/subjects/${subject}/versions/${version}")
  }

  override def deleteSchemaBySubjectAndVersion(subject: String, version: Int): Future[String] = {
    delete(s"${props.schemaRegistryUrl}/subjects/${subject}/versions/${version}")
  }

  override def checkCompatibilitySchema(name: String, payload: String): Future[String] = {
    post(payload,s"${props.schemaRegistryUrl}/compatibility/subjects/${name}/versions/latest")
  }

  override def getCompatibilityLevel(): Future[String] = {
    get(s"${props.schemaRegistryUrl}/config")
  }

  override def updateCompatibilityLevel(payload: String): Future[String] = {
    post(payload,s"${props.schemaRegistryUrl}/config")
  }

  override def updateCompatibilityLevelForSubject(name: String, payload: String): Future[String] = {
    post(payload,s"${props.schemaRegistryUrl}/config/${name}")
  }

  override def deleteSchemaBySubject(subject: String): Future[String] = {
    delete(s"${props.schemaRegistryUrl}/subjects/${subject}")
  }

  private def post(data: String, url:String): Future[String] = {
    sendData(data, url, HttpMethods.POST, contentType)
  }

  private  def put(data: String, url:String): Future[String] = {
    sendData(data, url, HttpMethods.PUT, contentType)
  }

  private  def get(url:String): Future[String] = {
    noBodiedRequest(url, HttpMethods.GET, contentType)
  }

  private  def delete(url:String): Future[String] = {
    noBodiedRequest(url, HttpMethods.DELETE, contentType)
  }

  private def sendData(data: String, url:String, method:HttpMethod, contentType:ContentType):Future[String] = {
    val responseFuture: Future[HttpResponse] =
      Http(system).singleRequest(
        HttpRequest(
          method,
          url,
          entity = HttpEntity(contentType, data.getBytes())
        )
      )
    //val html = Await.result(responseFuture.flatMap(x => Unmarshal(x.entity).to[String]), 5 seconds)//5 seconds
    //html
    responseFuture.flatMap(x => Unmarshal(x.entity).to[String])
  }

  private def noBodiedRequest(url:String,method:HttpMethod, contentType:ContentType): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http(system).singleRequest(HttpRequest(method,url))
    //val html = Await.result(responseFuture.flatMap(x => Unmarshal(x.entity).to[String]), props.timeout)
    //html
    responseFuture.flatMap(x => Unmarshal(x.entity).to[String])
  }


}

object  SchemaRegistryRepository{
  private val schemaRegistryMediaType = MediaType.custom("application/vnd.schemaregistry.v1+json",false)
  private lazy val contentType = ContentType(schemaRegistryMediaType, () => HttpCharsets.`UTF-8`)
}
