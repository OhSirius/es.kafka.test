package com.es.kafkatest.processes.schemaregistry.consumer

import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.schemaregistry.consumer.impl.ConsumerProps

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait IConsumerFactory[TMessage] {
  def create(props:ConsumerProps)():IConsumer[TMessage]//(implicit stag: ClassTag[TMessage]):IConsumer[TMessage]

}
