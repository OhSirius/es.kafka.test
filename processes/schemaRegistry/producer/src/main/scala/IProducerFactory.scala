package com.es.kafkatest.processes.schemaregistry.producer

import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.schemaregistry.producer.impl.ProducerProps

trait IProducerFactory[TMessage] {
  def create(props: ProducerProps):IProducer[TMessage]
}
