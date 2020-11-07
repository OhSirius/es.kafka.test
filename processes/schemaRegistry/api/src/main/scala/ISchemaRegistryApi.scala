package com.es.kafkatest.processes.schemaregistry.api

import scala.concurrent.Future

trait ISchemaRegistryApi {
  def createInitSubjects():Future[Boolean]
  def findScheme():Future[Boolean]
  def deleteAndRegisterScheme():Future[Boolean]
  def checkAndChangeCompatibilityScheme():Future[Boolean]
  def deleteAllScheme():Future[Boolean]
}
