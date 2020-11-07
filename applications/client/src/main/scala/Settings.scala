package com.es.kafkatest.app.client

import java.net.URI
import java.util.Map.Entry

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}

case class Settings() {
  var config:Config = null
  def initialize():Unit={
    config = ConfigFactory.load()//"application.dev.conf")//"application.conf")
  }

  lazy val kafkaServers:String = config.getString("client.kafka.zookeeper.servers")

  lazy val avroGenericProcessTopic = config.getString("client.processes.genericAvro.topic")

  lazy val schemaRegistryTopic = config.getString("client.processes.schemaRegistry.topic")

  lazy val schemaRegistry:String =  config.getString("client.kafka.schema.url")

  lazy val inputStreamsTopic = config.getString("client.processes.streams.topic")

  lazy val sqlServerConfigPath = "client.processes.sqlServer"

  lazy val PostgreSqlConfigPath = "client.processes.postgreSql"
  //redis
  lazy val redisServer: String = config.getString("server.redis.servers")
  lazy val redisPort: Int = config.getInt("server.redis.port")
  lazy val redisThemeQueue = config.getString("client.processes.redisQueue.themeQueue")
}
