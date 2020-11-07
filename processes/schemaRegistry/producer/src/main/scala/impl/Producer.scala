package com.es.kafkatest.processes.schemaregistry.producer.impl

import java.util.{Properties, UUID}

import com.es.kafkatest.processes.schemaregistry.producer.IProducer
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import org.apache.avro.generic.{GenericData, GenericRecord}
import com.es.kafkatest.inf.common.extensions.AvroExtensions._
import org.apache.avro.specific.SpecificDatumWriter
import java.io.ByteArrayOutputStream

import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Names
import org.apache.avro.io._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import com.es.kafkatest.inf.common.extensions.StringExtensions._
import com.es.kafkatest.inf.common.extensions.ResourcesExtensions._
import com.google.inject.Inject
import io.confluent.kafka.serializers.KafkaAvroSerializer

import scala.util.Random
//import org.apache.log4j.varia.NullAppender

import scala.util.Try

case class ProducerProps(val topic:String, val servers:String, val clientId:UUID, val schemaRegistryUrl:String)


class Producer[TMessage] @Inject() (@Assisted private val props:ProducerProps) extends IProducer[TMessage]{ //@Names("Props")
  private val _props = new Properties()
  _props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.servers)
  _props.put("schema.registry.url", props.schemaRegistryUrl)
  _props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  _props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,classOf[KafkaAvroSerializer].getCanonicalName)
  _props.put(ProducerConfig.CLIENT_ID_CONFIG, props.clientId.toString)
  private lazy val _producer =  new KafkaProducer[String,TMessage](_props)

  def send(message: TMessage): Unit = {
    assert(message != null, "Не определен исходный объект")
    assert(!props.topic.isNullOrEmpty, "Не задан topic для размещения данных")
    assert(!props.servers.isNullOrEmpty, "Не определены серверы Kafka для размещения данных")

    //Исправляет ошибку с логами
    //org.apache.log4j.BasicConfigurator.configure(new NullAppender)

    try {
      println(s"Producer отправляет данные ${message}")
      _producer.send(new ProducerRecord[String, TMessage](props.topic,Random.nextString(1), message),
        (metadata: RecordMetadata, e: Exception) => {
          if (e != null) e.printStackTrace()
          else println("The offset of the record we just sent is: " + metadata.offset)
        })
      _producer.flush
      println(s"Producer отправил данные")
    } catch {
      case ex: Exception =>
        println(ex.printStackTrace().toString)
        ex.printStackTrace()
    }

  }

  override def close(): Unit = _producer.close
}
