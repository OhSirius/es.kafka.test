package com.es.kafkatest.processes.redisconsumer.repositories

trait IRedisRepository[TValue] {
    def receive(): List[TValue]
    def commit()
}
