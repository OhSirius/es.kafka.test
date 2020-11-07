package com.es.kafkatest.processes.redisconsumer.serializer

import com.es.kafkatest.processes.schemaregistry.models.User

trait ISerializer[TValue] {
  def deserialize(bytes: Array[Byte]): TValue
}
