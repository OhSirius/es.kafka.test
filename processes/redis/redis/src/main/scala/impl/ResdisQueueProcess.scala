package com.es.kafkatest.processes.redis

import com.es.kafkatest.processes.redis.repositories._
import com.google.inject.Inject

class RedisQueueProcess[TValue] @Inject()(private val _repository : IRedisQueueRepository[TValue])
  extends IRedisQueueProcess[TValue]
{
  override def send(value: TValue): Unit = {

    _repository.push(value)
  }
}
