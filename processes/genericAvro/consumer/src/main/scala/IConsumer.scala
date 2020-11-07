package com.es.kafkatest.processes.genericavro.consumer

import com.es.kafkatest.inf.common.messages.IKafkaMessage
import scala.reflect.runtime.universe.{TypeTag}

import scala.reflect.ClassTag

trait IConsumer[TMessage<:Product with IKafkaMessage]  {
  def start():Unit
}
