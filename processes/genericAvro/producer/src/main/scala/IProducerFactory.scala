package com.es.kafkatest.processes.genericavro.producer

import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.genericavro.producer.impl.ProducerProps

trait IProducerFactory[TMessage<:Product with IKafkaMessage] {
  def create(props: ProducerProps):IProducer[TMessage]
}
