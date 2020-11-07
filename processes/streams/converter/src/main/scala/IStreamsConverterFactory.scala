package com.es.kafkatest.processes.streams.converters

import com.es.kafkatest.inf.common.messages.IKafkaConverter
import com.es.kafkatest.processes.streams.converters.impl.StreamsProps
import org.apache.avro.specific.SpecificRecord

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait IStreamsConverterFactory[TSource<:SpecificRecord with Product, TSink<:SpecificRecord with IKafkaConverter] {
  def create(props:StreamsProps)(implicit tag: TypeTag[TSink], ctag: ClassTag[TSink]):IStreamsConverter[TSource, TSink]
}
