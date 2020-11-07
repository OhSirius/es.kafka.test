package com.es.kafkatest.processes.schemaregistry.consumer.impl

import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.KafkaAvroDeserializer
import com.es.kafkatest.inf.common.extensions.Activator
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.schemaregistry.consumer.IConsumer
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.kafka.common.errors.TimeoutException

import scala.reflect.ClassTag
import com.es.kafkatest.processes.schemaregistry.models.User
import com.es.kafkatest.inf.common.extensions.Activator._
import com.es.kafkatest.inf.common.extensions.AvroExtensions._
import org.apache.avro.Schema

import scala.util.{Failure, Success, Try}
import scala.reflect._
import scala.reflect.runtime.universe.TypeTag


case class ConsumerProps(topic: String, groupId:String, servers: String, autoCommit: Boolean, commitInterval: Int, sessionTimeout: Int, consumerTimeout: Int, consumerPollTimeout: Int, val schemaRegistryUrl:String )

//https://stackoverflow.com/questions/44542328/kafka-api-java-io-ioexception-cant-resolve-address-xxx-x-x-xx9091
class Consumer[TMessage] @Inject() (@Assisted private val props:ConsumerProps)

  extends  IConsumer[TMessage]{
  private val _props = new Properties()
  _props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.servers)
  _props.put("schema.registry.url", props.schemaRegistryUrl)
  _props.put(ConsumerConfig.GROUP_ID_CONFIG, props.groupId.toString)
  _props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, props.autoCommit.toString)
  _props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, props.commitInterval.toString)
  _props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, props.sessionTimeout.toString)
  _props.put("consumer.timeout.ms", props.consumerTimeout.toString)
  _props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
  _props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,classOf[KafkaAvroDeserializer].getCanonicalName)
  _props.put("specific.avro.reader", "true")
  _props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")//"latest")//"earliest")
  private val consumer = new KafkaConsumer[String, TMessage](_props)

  var shouldRun : Boolean = true

  override def start(): Unit = {
    try {
      Runtime.getRuntime.addShutdownHook(new Thread(() => close()))

      consumer.subscribe(Collections.singletonList(props.topic))

      while (shouldRun) {
        val records: ConsumerRecords[String,  TMessage] = consumer.poll(props.consumerPollTimeout)
        val it = records.iterator()
        while(it.hasNext()) {
          println("Получаем сообщение из очереди.............")
          val record: ConsumerRecord[String,  TMessage] = it.next()
          println(s"Получено сообщение ${record.value}")
          consumer.commitSync
        }
      }
    }
    catch {
      case timeOutEx: TimeoutException =>
        println("Timeout ")
      case ex: Exception => ex.printStackTrace()
        println("Получена ошибка ")
    }
  }

  def close(): Unit = shouldRun = false


}
