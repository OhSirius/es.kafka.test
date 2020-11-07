package com.es.kafkatest.processes.redis.redisqueue.bootstrap

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.processes.redis._
import com.es.kafkatest.processes.redis.repositories._
import com.es.kafkatest.processes.redis.serializer._
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.{AbstractModule, Binder, Guice}
import net.codingwell.scalaguice.ScalaModule
import net.codingwell.scalaguice.InjectorExtensions._


case class QueueProps(val serverUrl: String,  val port: Int, themeQueue : String)


class RedisModule (private val props:QueueProps)
  extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder]
{
  override def configure(): Unit =
  {
    bind[QueueProps].toInstance(props)
    bind[ISerializer[User]].to[Serializer[User]]
    bind[IRedisQueueRepository[User]].to[RedisQueueRepository[User]]
    bind[IRedisQueueProcess[User]].to[RedisQueueProcess[User]]
  }
}

class RedisConfiguration(  private val props:QueueProps)
{
  def configure(): IRedisQueueProcess[User] = {
    val injector = Guice.createInjector(new RedisModule(props))
    val process =  injector.instance[IRedisQueueProcess[User]]
    process
  }
}
