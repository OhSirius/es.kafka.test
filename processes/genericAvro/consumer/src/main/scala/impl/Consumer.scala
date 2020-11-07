package com.es.kafkatest.processes.genericavro.consumer.impl

import java.util.{Collections, Properties}

import com.es.kafkatest.inf.common.extensions.Activator
import com.es.kafkatest.inf.common.messages.IKafkaMessage
import com.es.kafkatest.processes.genericavro.consumer.IConsumer
import com.es.kafkatest.processes.genericavro.models.User
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.typesafe.scalalogging.Logger
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.kafka.common.errors.TimeoutException
import org.slf4j.LoggerFactory

import scala.reflect.ClassTag
//import com.es.kafkatest.processes.genericavro.models.User._
import com.es.kafkatest.inf.common.extensions.Activator._
import com.es.kafkatest.inf.common.extensions.AvroExtensions._
import org.apache.avro.Schema

import scala.util.{Failure, Success, Try}
import scala.reflect._
import scala.reflect.runtime.universe.{TypeTag}

case class ConsumerProps(topic: String, groupId:String, servers: String, autoCommit: Boolean, commitInterval: Int, sessionTimeout: Int, consumerTimeout: Int, consumerPollTimeout: Int )

//https://stackoverflow.com/questions/44542328/kafka-api-java-io-ioexception-cant-resolve-address-xxx-x-x-xx9091
class Consumer[TMessage<:Product with IKafkaMessage] @Inject() (@Assisted private val props:ConsumerProps,
                                                                @Assisted implicit val tag: TypeTag[TMessage],
                                                                @Assisted implicit val ctag: ClassTag[TMessage])

  extends  IConsumer[TMessage]{
  private val _props = new Properties()
  _props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.servers)
  _props.put(ConsumerConfig.GROUP_ID_CONFIG, props.groupId.toString)
  _props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, props.autoCommit.toString)
  _props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, props.commitInterval.toString)
  _props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, props.sessionTimeout.toString)
  _props.put("consumer.timeout.ms", props.consumerTimeout.toString)
  _props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
  _props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,classOf[ByteArrayDeserializer].getCanonicalName)
  private val consumer = new KafkaConsumer[String, Array[Byte]](_props)
  private lazy val logger = Logger(LoggerFactory.getLogger(this.getClass))

  var shouldRun : Boolean = true


  override def start(): Unit = {
    try {
      Runtime.getRuntime.addShutdownHook(new Thread(() => close()))

      consumer.subscribe(Collections.singletonList(props.topic))

      while (shouldRun) {
        val records: ConsumerRecords[String,  Array[Byte]] = consumer.poll(props.consumerPollTimeout)
        val it = records.iterator()
        while(it.hasNext()) {
          println("Получаем сообщение из очереди.............")
          logger.info("Получаем сообщение из очереди.............")
          val record: ConsumerRecord[String,  Array[Byte]] = it.next()

          //val bytes = record.value()
          //val text = (bytes.map(_.toChar)).mkString
          //println(s"Saw Text ${text}")
          if(record.value().isEmpty){
            println(s"Получено сообщение <ПУСТОЕ>")
            logger.info(s"Получено сообщение <ПУСТОЕ>")
          }else {
            val message = deserialize(record.value())
            if(message.isFailure)
              {
                throw  new IllegalArgumentException(s"Не удалось прочитать сообщение ${message}")
                logger.info(s"Не удалось прочитать сообщение ${message}")
              }


            println(s"Получено сообщение ${message}")
            logger.info(s"Получено сообщение ${message}")
          }
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


  private def deserialize(message: Array[Byte]): Try[TMessage] = {

    //https://stackoverflow.com/questions/10726528/scala-is-it-possible-to-constrain-a-type-parameter-to-be-non-abstract
    // Десериализуем сообщение
    val reader: DatumReader[GenericRecord] =
      new SpecificDatumReader[GenericRecord](Activator.instanceOf[TMessage]().schema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
    val messageData: GenericRecord = reader.read(null, decoder)
    //messageData.
    //Avro.toMap

//    // Make user object
//    val finalUser = Try[User](
//      User(userData.get("id").toString.toInt, userData.get("name").toString, try {
//        Some(userData.get("email").toString)
//      } catch {
//        case _ => None
//      })
//    )
    //Создаем объект
    val finalMessage = Try[TMessage](
      messageData.convertToMap().convert[TMessage]
    )

//    finalMessage match {
//      case Success(u) =>
//        Some(u)
//      case Failure(e) =>
//        None
//    }
    finalMessage
  }

}
