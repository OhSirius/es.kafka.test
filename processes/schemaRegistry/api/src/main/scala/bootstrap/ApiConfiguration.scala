package com.es.kafkatest.processes.schemaregistry.api.bootstrap

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.schemaregistry.api.{ISchemaRegistryApi, ISchemaRegistryRepository}
import com.es.kafkatest.processes.schemaregistry.api.impl.{Props, SchemaRegistryApi, SchemaRegistryRepository}
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

class ApiModule(private val props:Props) extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  override def configure() = {
    //bindFactory[IConsumer[TMessage], Consumer[TMessage], IConsumerFactory[TMessage]]
    bind[Props].toInstance(props)
    bind[ISchemaRegistryRepository].to[SchemaRegistryRepository]
    bind[ISchemaRegistryApi].to[SchemaRegistryApi]
  }
}

class ApiConfiguration(private val props:Props) {
  def configure(): ISchemaRegistryApi = {
    val injector = Guice.createInjector(new ApiModule(props))
    val producerFactory = injector.instance[ISchemaRegistryApi]
    producerFactory
  }
}