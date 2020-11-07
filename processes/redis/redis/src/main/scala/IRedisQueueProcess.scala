package com.es.kafkatest.processes.redis

trait IRedisQueueProcess[TValue] {
    def send(value: TValue)  : Unit
}
