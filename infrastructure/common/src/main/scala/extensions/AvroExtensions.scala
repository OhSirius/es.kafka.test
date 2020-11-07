package com.es.kafkatest.inf.common.extensions


import com.es.kafkatest.inf.common.messages.IKafkaMessage
import org.apache.avro.Schema.Field
//import org.apache.avro.Field
import org.apache.avro.generic.{GenericData, GenericRecord}
import scala.collection.JavaConverters._
import scala.reflect._
import scala.reflect.runtime.universe._
import org.apache.avro.util._

object AvroExtensions {

  //Конверитируем событие в Avro-событие
  //implicit class AvroMessage[TMessage<:Product with IKafkaMessage](val message: TMessage) extends AnyVal {
  implicit class AvroMessage(val message: Product with IKafkaMessage) extends AnyVal {
    def convertToRecord():GenericRecord = {
      val avroMessage: GenericRecord = new GenericData.Record(message.schema)
      for ((key,value)<-message.getClass.getDeclaredFields.map(_.getName).zip(message.productIterator.to).toMap){
        //if(message.schema.getField(key).get.get. <:<  classTag(Option[Any]))

        if(value.isInstanceOf[Option[Any]]){
          avroMessage.put(key,value.asInstanceOf[Option[Any]].orNull)
        }
        else
          avroMessage.put(key,value)

      }
      avroMessage
    }
  }

  //https://stackoverflow.com/questions/6356465/how-to-get-scala-list-from-java-list
  implicit class GenericRecordMessage(val message: GenericRecord) extends AnyVal {
    def convertToMap():Map[String,Any] = {
      var mapMessage: Map[String, Any] = Map.empty[String, Any]
      for (field:Field <-message.getSchema.getFields.asScala.toArray){
        mapMessage += (field.name -> getScalaValue(message.get(field.name)))//.asInstanceOf[AnyRef]
      }
      mapMessage
    }

    //Конвертируем Kafka-типы в Scala-типы
    private def getScalaValue(value:AnyRef):Any = {
      if (value.isInstanceOf[Utf8])
        value.toString//.asInstanceOf[String]
      else if (value.isInstanceOf[Integer])
        value.asInstanceOf[Int]
        //value.toString.toInt.asInstanceOf[Any]
    }

    //implicit def integer2Int(x: Integer):Int =
    //  java.lang.Integer.valueOf(x)

  }

}
