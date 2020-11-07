package com.es.kafkatest.processes.schemaregistry.producer

import com.es.kafkatest.inf.common.messages.IKafkaMessage

trait IProducer[TMessage] {
  def send(message:TMessage):Unit
  def close():Unit
}
