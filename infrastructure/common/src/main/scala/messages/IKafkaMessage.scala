package com.es.kafkatest.inf.common.messages

import org.apache.avro.Schema

trait IKafkaMessage {
  def schema:Schema
}
