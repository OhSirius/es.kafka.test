package com.es.kafkatest.processes.streams.converters.bootstrap

import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.inf.common.messages.{IKafkaConverter, IKafkaMessage}
import com.es.kafkatest.processes.streams.converters.{IStreamsConverter, IStreamsConverterFactory}
import com.es.kafkatest.processes.streams.converters.impl.StreamsConverter
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.apache.avro.specific.SpecificRecord

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

class StreamsModule[TSource<:SpecificRecord with Product:TypeTag:ClassTag, TSink<:SpecificRecord with IKafkaConverter:TypeTag:ClassTag]() extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  override def configure() = {
    //bind[IStreamsConverter[TSource, TSink]].to[StreamsConverter[TSource, TSink]]
    bindFactory[IStreamsConverter[TSource, TSink], StreamsConverter[TSource, TSink], IStreamsConverterFactory[TSource, TSink]]
  }
}

class StreamsConverterConfiguration[TSource<:SpecificRecord with Product:TypeTag:ClassTag, TSink<:SpecificRecord with IKafkaConverter:TypeTag:ClassTag]() {
  def configure(): IStreamsConverterFactory[TSource, TSink] = {
    val injector = Guice.createInjector(new StreamsModule[TSource, TSink]())
    val producerFactory = injector.instance[IStreamsConverterFactory[TSource, TSink]]
    producerFactory
  }
}
