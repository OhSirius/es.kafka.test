package com.es.kafkatest.processes.redis


import com.es.kafkatest.processes.redis.repositories.IRedisClientRepository
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RedisProcess[TKey] @Inject() (private val _repository : IRedisClientRepository[TKey,User])
      extends IRedisProcess[TKey, User]
 {

   override  def put(user: User): Future[Boolean] = {
     val isPut: Future[Boolean] = Future {
       _repository.putRedis(user)
     }
     isPut
   }

   override def pull(key: TKey): Future[User] = {
     val isPut: Future[User] = Future {
       _repository.getRedis(key)
     }
     isPut
   }
 }


