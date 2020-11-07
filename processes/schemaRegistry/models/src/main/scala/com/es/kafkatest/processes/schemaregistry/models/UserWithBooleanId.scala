/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package com.es.kafkatest.processes.schemaregistry.models

import scala.annotation.switch

case class UserWithBooleanId(var id: Boolean) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(false)
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        id
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.id = {
        value
      }.asInstanceOf[Boolean]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = UserWithBooleanId.SCHEMA$
}

object UserWithBooleanId {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"UserWithBooleanId\",\"namespace\":\"com.es.kafkatest.processes.schemaregistry.models\",\"fields\":[{\"name\":\"id\",\"type\":\"boolean\"}]}")
}