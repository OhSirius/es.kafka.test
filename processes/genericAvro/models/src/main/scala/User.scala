package com.es.kafkatest.processes.genericavro.models

import com.es.kafkatest.inf.common.messages.IKafkaMessage
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser

import scala.io.Source


case class User (id: Int, name: String, email: Option[String]) extends IKafkaMessage{
  import User._
  def this(){
    this(0,"", Some(""))
  }

  override def schema: Schema = _schema
}

object User {
  private lazy val _schema:Schema = new Parser().parse(Source.fromURL(getClass.getResource("/userSchema.avsc")).mkString)
}

