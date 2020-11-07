package com.es.kafkatest.processes.redis.repositories

import com.es.kafkatest.processes.schemaregistry.models.User

import scala.concurrent.Future
import scala.reflect.runtime.universe.{TypeTag}
import scala.reflect.ClassTag

//trait IRedisClientRepository[TKey<:AnyVal, TValue]
trait IRedisClientRepository[TKey, TValue]
//trait IRedisClientRepository[TValue]
{
    def putRedis(user: TValue) : Boolean
    def getRedis(key: TKey) : TValue
}

trait  IRedisQueueRepository[TValue]
{
    def push(user: TValue): Unit
}
