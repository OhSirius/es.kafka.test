package com.es.kafkatest.processes.schemaregistry.models

import com.es.kafkatest.inf.common.messages.IKafkaConverter

class SinkUser extends UserWithBooleanId with IKafkaConverter{
  //def this(id:Boolean)=super()
  override def fill(map: Map[String, Any]): Unit = {
    if(map==null||map.isEmpty)
      return

    for ((k,v)<-map){
      println(s"key=${k}-value=${v}")
    }
  }
}