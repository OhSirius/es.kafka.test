package com.es.kafkatest.processes.genericavro.consumer.bootstrap

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.genericavro.consumer.impl.Consumer
import com.es.kafkatest.processes.genericavro.consumer.{IConsumer, IConsumerFactory}
import com.google.inject.assistedinject.{FactoryModuleBuilder, FactoryProvider}
import com.google.inject._
import net.codingwell.scalaguice.{ScalaModule, typeLiteral}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.reflect._
import net.codingwell.scalaguice.InjectorExtensions._

//https://google.github.io/guice/api-docs/3.0/javadoc/com/google/inject/assistedinject/FactoryModuleBuilder.html
//https://stackoverflow.com/questions/39344963/guice-unable-to-bind-classtagt-to-know-the-name-of-t-class
class ConsumerModule[TMessage<:Product with IKafkaMessage:TypeTag:ClassTag]() extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  override def configure() = {
    //binder.bind(classOf[ProducerProps]).annotatedWith(Names.named("Props")).toInstance(new ProducerProps(topic, servers, UUID.randomUUID()))
    //binder.bind(classOf[IConsumer[TMessage]]).to(classOf[Consumer[TMessage]])
    //binder.bind(classOf[IConsumerFactory[TMessage]]).toProvider(FactoryProvider.newFactory(classOf[IConsumerFactory[TMessage]], classOf[Consumer[TMessage]]))
    //val test = classTag[TMessage]

    //install(new FactoryModuleBuilder()
    //  //.implement(classOf[IProducer[TMessage]], classOf[IProducer[TMessage]])
    //  .implement(typeLiteral[IConsumer[TMessage]], typeLiteral[Consumer[TMessage]])
    //  //.build(classOf[IProducerFactory[TMessage]]))
    // .build(typeLiteral[IConsumerFactory[TMessage]]))
    bindFactory[IConsumer[TMessage], Consumer[TMessage], IConsumerFactory[TMessage]]
  }
}

class ConsumerConfiguration[TMessage<:Product with IKafkaMessage:TypeTag:ClassTag]() {
  def configure(): IConsumerFactory[TMessage] = {
    //val injector = Guice.createInjector(new ConsumerModule())
    //val producerFactory = injector.getInstance(Key.get(typeLiteral[IConsumerFactory[TMessage]]))
    val injector = Guice.createInjector(new ConsumerModule())
    val producerFactory = injector.instance[IConsumerFactory[TMessage]]
    producerFactory
  }
}