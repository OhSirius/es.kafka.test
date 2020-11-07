package com.es.kafkatest.processes.redis.bootstrap


import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.processes.redis._
import com.es.kafkatest.processes.redis.repositories._
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.{AbstractModule, Binder, Guice}
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import net.codingwell.scalaguice.InjectorExtensions._

case class Props ( val serverUrl: String,  val port: Int)

class RedisModule (private val props:Props)
  extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder]
{
  override def configure(): Unit =
  {
    bind[Props].toInstance(props)

    bind[IRedisClientRepository[Int,User]].to[RedisClientRepository]
    bind[IRedisProcess[Int, User]].to[RedisProcess[Int]]
  }
}

class RedisConfiguration(  private val props:Props)
{
  def configure(): IRedisProcess[Int,User] = {
    val injector = Guice.createInjector(new RedisModule(props))
    val process =  injector.instance[IRedisProcess[Int,User]]
    process
  }
}

