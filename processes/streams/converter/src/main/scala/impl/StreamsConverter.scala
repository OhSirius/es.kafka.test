package com.es.kafkatest.processes.streams.converters.impl

import com.es.kafkatest.processes.streams.converters.IStreamsConverter
import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.common.serialization.{Serde, Serdes, StringDeserializer}
import org.apache.kafka.streams.kstream.{KStream, Produced}

import scala.concurrent.TimeoutException
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}
import com.es.kafkatest.inf.common.extensions.Activator
import com.es.kafkatest.inf.common.extensions.Activator._
import com.es.kafkatest.inf.common.messages.IKafkaConverter
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted

import scala.reflect.runtime.universe.TypeTag
import scala.reflect.ClassTag

case class StreamsProps(topic: String, servers: String, schemaRegistryUrl:String )

class StreamsConverter[TSource<:SpecificRecord with Product, TSink<:SpecificRecord with IKafkaConverter]  @Inject()  (@Assisted private val props:StreamsProps,
                                                                  @Assisted implicit val tag: TypeTag[TSink],
                                                                  @Assisted implicit val ctag: ClassTag[TSink]) extends IStreamsConverter[TSource, TSink] {

  val builder: StreamsBuilder = new StreamsBuilder()
  var streams: Option[KafkaStreams] = None

  val streamsConfiguration: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, props.topic)//"avro-stream-demo-topic-streams"
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, props.servers)//"localhost:9092"
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, props.schemaRegistryUrl )//"http://localhost:8081"
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  override def start(inTopic: String, outTopic: String): Unit = {
    try {
      Runtime.getRuntime.addShutdownHook(new Thread(() => close()))

      //https://github.com/confluentinc/kafka-streams-examples/blob/4.1.x/src/test/scala/io/confluent/examples/streams/SpecificAvroScalaIntegrationTest.scala

      // Write the input data as-is to the output topic.
      //
      // If
      //
      // a) we have already configured the correct default serdes for keys and
      // values
      //
      // b) the types for keys and values are the same for both the input topic and the
      // output topic
      //
      // We would only need to define:
      //
      //   builder.stream(inputTopic).to(outputTopic);
      //
      // However, in the code below we intentionally override the default serdes in `to()` to
      // demonstrate how you can construct and configure a specific Avro serde manually.
      val stringSerde: Serde[String] = Serdes.String
      val sourceSerde: Serde[TSource] = new SpecificAvroSerde[TSource]
      val sinkSerde: Serde[TSink] = new SpecificAvroSerde[TSink]

      // Note how we must manually call `configure()` on this serde to configure the schema registry
      // url.  This is different from the case of setting default serdes (see `streamsConfiguration`
      // above), which will be auto-configured based on the `StreamsConfiguration` instance.
      val isKeySerde: Boolean = false
      sourceSerde.configure(
        Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
          props.schemaRegistryUrl), isKeySerde)
      sinkSerde.configure(
        Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
          props.schemaRegistryUrl), isKeySerde)


      val stream: KStream[String, TSource] = builder.stream(inTopic)

      val mappedStream  =
        stream.map[String, TSink]((k,v) => {
          println(s"Streams получил сообщение ============ ${v}")
          //println(s"Saw User ${v}")
          //new KeyValue(k, UserWithUUID(v.id,v.name, java.util.UUID.randomUUID().toString()))
          val value = Activator.instanceOf[TSink]
          value.fill(v.convertToMap)
          new KeyValue(k, value)
        })

      //Отправляем TSink в output topic
      mappedStream.to(outTopic, Produced.`with`(stringSerde, sinkSerde))
      streams = Some(new KafkaStreams(builder.build(), streamsConfiguration))
      streams.map(_.start())

    }
    catch {
      case timeOutEx: TimeoutException =>
        println("Timeout ")
        false
      case ex: Exception => ex.printStackTrace()
        println("Got error when reading message ")
        false
    }
  }

  def close(): Unit = streams.map(_.close())

}
