package com.es.kafkatest.processes.genericavro.producer

import com.es.kafkatest.inf.common.messages.IKafkaMessage

trait IProducer[TMessage<:Product with IKafkaMessage] {
  def send(message:TMessage):Unit
  def close():Unit
}
