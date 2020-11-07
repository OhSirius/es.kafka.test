package com.es.kafkatest.processes.schemaregistry.consumer

import com.es.kafkatest.inf.common.messages.IKafkaMessage

trait IConsumer[TMessage]  {
  def start():Unit
}
