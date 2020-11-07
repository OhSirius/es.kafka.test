package com.es.kafkatest.processes.schemaregistry.producer.bootstrap

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.schemaregistry.producer.impl.Producer
import com.es.kafkatest.processes.schemaregistry.producer.{IProducer, IProducerFactory}
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag


class ProducerModule[TMessage:TypeTag]() extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
    override def configure(): Unit = {

    bindFactory[IProducer[TMessage], Producer[TMessage], IProducerFactory[TMessage]]
  }
}

class ProducerConfiguration[TMessage:TypeTag:ClassTag]() {
  def configure(): IProducerFactory[TMessage] = {
    val injector = Guice.createInjector(new ProducerModule())
    val producerFactory = injector.instance[IProducerFactory[TMessage]]
    producerFactory
  }
}