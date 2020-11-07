/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package com.es.kafkatest.processes.schemaregistry.models

import scala.annotation.switch

case class User(var id: Int, var name: String) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(0, "")
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        id
      }.asInstanceOf[AnyRef]
      case 1 => {
        name
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.id = {
        value
      }.asInstanceOf[Int]
      case 1 => this.name = {
        value.toString
      }.asInstanceOf[String]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = User.SCHEMA$
}

object User {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"User\",\"namespace\":\"com.es.kafkatest.processes.schemaregistry.models\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"}]}")
}