//package com.es.kafkatest.processes.schemaregistry.models
//package com.es.kafkatest.processes.schemaregistry.models
package com.sul.distributedRepository.models

//package com.sul.distributedRepository.models

import scala.annotation.switch

case class IdentityUser(var id: Int, var userName: String, var passwordHash: String, var email: String, var phone: String, var maxConnection: Int) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(0, "", "", "", "", 0)
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        id
      }.asInstanceOf[AnyRef]
      case 1 => {
        userName
      }.asInstanceOf[AnyRef]
      case 2 => {
        passwordHash
      }.asInstanceOf[AnyRef]
      case 3 => {
        email
      }.asInstanceOf[AnyRef]
      case 4 => {
        phone
      }.asInstanceOf[AnyRef]
      case 5 => {
        maxConnection
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.id = {
        value
      }.asInstanceOf[Int]
      case 1 => this.userName = {
        value.toString
      }.asInstanceOf[String]
      case 2 => this.passwordHash = {
        value.toString
      }.asInstanceOf[String]
      case 3 => this.email = {
        value.toString
      }.asInstanceOf[String]
      case 4 => this.phone = {
        value.toString
      }.asInstanceOf[String]
      case 5 => this.maxConnection = {
        value
      }.asInstanceOf[Int]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = IdentityUser.SCHEMA$
}

object IdentityUser {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"IdentityUser\",\"namespace\":\"com.sul.distributedRepository.models\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"userName\",\"type\":\"string\"},{\"name\":\"passwordHash\",\"type\":\"string\"},{\"name\":\"email\",\"type\":\"string\"},{\"name\":\"phone\",\"type\":\"string\"},{\"name\":\"maxConnection\",\"type\":\"int\"}]}")
}
