package com.es.kafkatest.processes.streams.converters

import com.es.kafkatest.inf.common.messages.IKafkaConverter
import org.apache.avro.specific.SpecificRecord

trait IStreamsConverter[TSource<:SpecificRecord with Product, TSink<:SpecificRecord with IKafkaConverter] {
  def start(inTopic:String, outTopic:String)
}
