package com.es.kafkatest.processes.database.sqlServer

//https://www.scala-lang.org/api/2.12.x/scala/Enumeration.html
object RepTypes extends Enumeration{
  type RepTypes = Value
  val DirectSql, Slick = Value
}