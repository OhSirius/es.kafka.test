package com.easysales.kafkatest.databases.common

//https://www.scala-lang.org/api/2.12.x/scala/Enumeration.html
object RepTypes extends Enumeration{
  type RepTypes = Value
  val DirectSql, Slick = Value
}
