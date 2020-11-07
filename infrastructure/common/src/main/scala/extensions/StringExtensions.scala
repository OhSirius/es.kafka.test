package com.es.kafkatest.inf.common.extensions

object StringExtensions {

  //Конверитируем событие в Avro-событие
  implicit class StringEx(val str: String) extends AnyVal {
    def isNullOrEmpty():Boolean = {
      if(str==null || str.trim.isEmpty)
        return  true

      false
    }
  }
}
