package com.es.kafkatest.processes.redis.serializer

trait ISerializer[TValue] {
  def serialize(value: TValue): Array[Byte]

}
