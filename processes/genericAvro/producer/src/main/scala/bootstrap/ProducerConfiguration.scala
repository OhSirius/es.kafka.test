package com.es.kafkatest.processes.genericavro.producer.bootstrap

import java.util.UUID

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.google.inject.assistedinject.{FactoryModuleBuilder, FactoryProvider}
import com.google.inject._
import com.es.kafkatest.processes.genericavro.producer.{IProducer, IProducerFactory}
import com.es.kafkatest.processes.genericavro.producer.impl.{Producer, ProducerProps}
import com.google.inject.name.Names
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import net.codingwell.scalaguice.{ScalaModule, ScalaPrivateModule}
import net.codingwell.scalaguice.typeLiteral

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import net.codingwell.scalaguice.InjectorExtensions._

//https://google.github.io/guice/api-docs/3.0/javadoc/com/google/inject/assistedinject/FactoryModuleBuilder.html
//https://github.com/codingwell/scala-guice/ для generic-ов
//https://stackoverflow.com/questions/6271435/guice-and-scala-injection-on-generics-dependencies
//https://www.programcreek.com/scala/net.codingwell.scalaguice.ScalaModule
//https://stackoverflow.com/questions/46405050/guice-injection-with-generics-named-issues
//https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/assistedinject/FactoryModuleBuilder.html
//https://gist.github.com/noel-yap/3784775
//http://www.tzavellas.com/techblog/2010/08/22/making-guice-more-scala-friendly/
//https://github.com/codingwell/scala-guice/issues/20
class ProducerModule[TMessage<:Product with IKafkaMessage:TypeTag]() extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  //override def configure(binder: Binder): Unit = {
  override def configure(): Unit = {
    //binder.bind(classOf[ProducerProps]).annotatedWith(Names.named("Props")).toInstance(new ProducerProps(topic, servers, UUID.randomUUID()))
    //binder.bind(new TypeLiteral[IProducer[TMessage]]).to(classOf[Producer[TMessage]])
    //binder.bind(classOf[IProducerFactory[TMessage]]).toProvider(FactoryProvider.newFactory(classOf[IProducerFactory[TMessage]], classOf[IProducer[TMessage]]))
    //bind(classOf[ProducerProps]).annotatedWith(Names.named("Props")).toInstance(new ProducerProps(topic, servers, UUID.randomUUID()))
    //bind[IProducer[TMessage]].to[Producer[TMessage]]
    //bind(classOf[IProducer[TMessage]]).to(classOf[Producer[TMessage]])
    //bind[IProducerFactory[TMessage]].toProvider(FactoryProvider.newFactory(classOf[IProducerFactory[TMessage]], classOf[IProducer[TMessage]]))
    //install(new FactoryModuleBuilder()
    //  //.implement(classOf[IProducer[TMessage]], classOf[IProducer[TMessage]])
    //  .implement(new TypeLiteral[IProducer[TMessage]]{}, new TypeLiteral[Producer[TMessage]]{})
    //  //.build(classOf[IProducerFactory[TMessage]]))
    //  .build(new TypeLiteral[IProducerFactory[TMessage]]{}))
    //  //.build(classOf[IProducerFactory[TMessage]]))

    //install(new FactoryModuleBuilder()
    //  //.implement(classOf[IProducer[TMessage]], classOf[IProducer[TMessage]])
    //  .implement(typeLiteral[IProducer[TMessage]], typeLiteral[Producer[TMessage]])
    //  //.build(classOf[IProducerFactory[TMessage]]))
    //  .build(typeLiteral[IProducerFactory[TMessage]]))
    bindFactory[IProducer[TMessage], Producer[TMessage], IProducerFactory[TMessage]]
  }
}

class ProducerConfiguration[TMessage<:Product with IKafkaMessage:TypeTag:ClassTag]() {
  def configure(): IProducerFactory[TMessage] = {
    //val injector = Guice.createInjector(new ProducerModule())
    ////val producerFactory = injector.getInstance(classOf[IProducerFactory[TMessage]])
    //val producerFactory = injector.getInstance(Key.get(typeLiteral[IProducerFactory[TMessage]]))
    val injector = Guice.createInjector(new ProducerModule())
    val producerFactory = injector.instance[IProducerFactory[TMessage]]
    producerFactory
  }
}