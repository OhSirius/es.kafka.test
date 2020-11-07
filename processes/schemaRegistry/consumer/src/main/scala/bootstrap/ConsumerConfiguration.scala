package com.es.kafkatest.processes.schemaregistry.consumer.bootstrap

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.schemaregistry.consumer.impl.Consumer
import com.es.kafkatest.processes.schemaregistry.consumer.{IConsumer, IConsumerFactory}
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

class ConsumerModule[TMessage:TypeTag:ClassTag]() extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  override def configure() = {
    bindFactory[IConsumer[TMessage], Consumer[TMessage], IConsumerFactory[TMessage]]
  }
}

class ConsumerConfiguration[TMessage:TypeTag:ClassTag]() {
  def configure(): IConsumerFactory[TMessage] = {
    val injector = Guice.createInjector(new ConsumerModule())
    val producerFactory = injector.instance[IConsumerFactory[TMessage]]
    producerFactory
  }
}