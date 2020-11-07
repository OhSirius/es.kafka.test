package com.es.kafkatest.processes.genericavro.producer.impl

import java.util.{Properties, UUID}

import com.es.kafkatest.processes.genericavro.producer.IProducer
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import org.apache.avro.generic.{GenericData, GenericRecord}
import com.es.kafkatest.inf.common.extensions.AvroExtensions._
import org.apache.avro.specific.SpecificDatumWriter
import java.io.ByteArrayOutputStream

import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Names
import org.apache.avro.io._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import com.es.kafkatest.inf.common.extensions.StringExtensions._
import com.es.kafkatest.inf.common.extensions.ResourcesExtensions._
import com.google.inject.Inject
//import org.apache.log4j.varia.NullAppender

import scala.util.Try

case class ProducerProps(val topic:String, val servers:String, val clientId:UUID)

//https://stackoverflow.com/questions/1140358/how-to-initialize-log4j-properly
class Producer[TMessage<:Product with IKafkaMessage] @Inject() (@Assisted private val props:ProducerProps) extends IProducer[TMessage]{ //@Names("Props")
  private val _props = new Properties()
  _props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.servers)// "bootstrap.servers"
  _props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  _props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,classOf[ByteArraySerializer].getCanonicalName)
  _props.put(ProducerConfig.CLIENT_ID_CONFIG, props.clientId.toString)// "client.id"
  _props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd")
  private lazy val _producer =  new KafkaProducer[String,Array[Byte]](_props)

  def send(message: TMessage): Unit = {
    assert(message != null, "Не определен исходный объект")
    assert(!props.topic.isNullOrEmpty, "Не задан topic для размещения данных")
    assert(!props.servers.isNullOrEmpty, "Не определены серверы Kafka для размещения данных")

    //Исправляет ошибку с логами
    //org.apache.log4j.BasicConfigurator.configure(new NullAppender)

    try {
      val serializedBytes = serialize(message)
      println(s"Producer отправляет данные ${serializedBytes.toString}")
      _producer.send(new ProducerRecord[String, Array[Byte]](props.topic, serializedBytes))
      _producer.flush
      println(s"Producer отправил данные")
    } catch {
      case ex: Exception =>
        println(ex.printStackTrace().toString)
        ex.printStackTrace()
    }

  }

  //Используем монаду Maybe https://darrenjw.wordpress.com/2016/04/15/first-steps-with-monads-in-scala/
  //https://markhneedham.com/blog/2011/09/15/scala-for-comprehensions-with-options/
  //https://www.programcreek.com/scala/org.apache.avro.generic.GenericRecord
  private def serialize(message:TMessage): Array[Byte] = {
    //var serializedBytes: Array[Byte] = null
    //var encoder: Option[BinaryEncoder] = None
    //var out : Option[ByteArrayOutputStream] = None
    //try{
    //Сериализуем сообщение в массив байт

    usingStream(new ByteArrayOutputStream())(out => {
      val encoder = EncoderFactory.get.binaryEncoder(out, null)
      val writer = new SpecificDatumWriter[GenericRecord](message.schema)
      writer.write(message.convertToRecord, encoder)
      //serializedBytes = out.get.toByteArray
      //val out = new ByteArrayOutputStream()
      //val encoder = EncoderFactory.get.binaryEncoder(out, null)
      //writer.write(message.convertToRecord, encoder)

      encoder.flush
      //out.close
      out.toByteArray
    })
    //}
    //finally{
    //  //for (enc <-encoder) yield enc.flush
    //  for (o <- out) yield o.close
    //}

    //serializedBytes
  }

  override def close(): Unit = _producer.close
}
