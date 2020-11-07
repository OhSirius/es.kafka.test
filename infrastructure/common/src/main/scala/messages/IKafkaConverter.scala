package com.es.kafkatest.inf.common.messages

trait IKafkaConverter {
  def fill(map: Map[String, Any]):Unit
}
