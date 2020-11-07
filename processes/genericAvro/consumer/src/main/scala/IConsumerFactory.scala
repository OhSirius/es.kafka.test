package com.es.kafkatest.processes.genericavro.consumer

import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.genericavro.consumer.impl.ConsumerProps
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.ClassTag

trait IConsumerFactory[TMessage<:Product with IKafkaMessage] {
  def create(props:ConsumerProps)(implicit tag: TypeTag[TMessage], ctag: ClassTag[TMessage]):IConsumer[TMessage]//(implicit stag: ClassTag[TMessage]):IConsumer[TMessage]

}
