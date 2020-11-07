package com.es.kafkatest.processes.schemaregistry.api

import akka.http.scaladsl.model.ContentType

import scala.concurrent.Future

trait ISchemaRegistryRepository {
  def createSubject(name:String, payload:String):Future[String]
  def getSubjects():Future[String]
  def getSchemaById(id: Int):Future[String]
  def getSchemaBySubject(subject:String):Future[String]
  def getSchemaBySubjectAndVersion(subject:String, version:Int):Future[String]
  def deleteSchemaBySubjectAndVersion(subject:String, version:Int):Future[String]
  def checkCompatibilitySchema(name:String, payload:String):Future[String]
  def getCompatibilityLevel():Future[String]
  def updateCompatibilityLevel(payload:String):Future[String]
  def updateCompatibilityLevelForSubject(name:String, payload:String):Future[String]
  def deleteSchemaBySubject(subject:String):Future[String]

}
