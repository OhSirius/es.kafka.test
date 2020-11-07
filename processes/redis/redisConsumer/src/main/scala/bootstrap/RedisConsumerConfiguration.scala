package com.es.kafkatest.processes.redisconsumer.bootstrap


import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.processes.redisconsumer.process._
import com.es.kafkatest.processes.redisconsumer.repositories._
import com.es.kafkatest.processes.redisconsumer.serializer._
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.{AbstractModule, Binder, Guice}
import net.codingwell.scalaguice.ScalaModule
import net.codingwell.scalaguice.InjectorExtensions._

case class Props(val serverUrl: String,  val port: Int, val themeQueue: String)

class RedisModule (private val props:Props)
  extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder]
{
  override def configure(): Unit =
  {
    bind[Props].toInstance(props)

    bind[ISerializer[User]].to[Serializer[User]]
    bind[IRedisRepository[User]].to[RedisRepository[User]]
    bind[IRedisConsumerProcess].to[RedisConsumerProcess]
  }
}

class RedisConsumerConfiguration (  private val props:Props)
{
  def configure(): IRedisConsumerProcess = {
    val injector = Guice.createInjector(new RedisModule(props))
    val process = injector.instance[IRedisConsumerProcess]
    return process
  }

}


