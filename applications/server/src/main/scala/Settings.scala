package com.es.kafkatest.app.server

import java.net.URI
import java.util.Map.Entry

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}


case class Settings() {
  private var config:Config = null
  def initialize():Unit={
    config = ConfigFactory.load()//("app.conf")
  }

  lazy val kafkaServers:String = config.getString("server.kafka.zookeeper.servers")
  lazy val schemaRegistry:String =  config.getString("server.kafka.schema.url")

  //avroGeneric
  lazy val avroGenericProcessTopic = config.getString("server.processes.genericAvro.topic")
  lazy val avroGenericGroup = config.getString("server.processes.genericAvro.group")
  lazy val avroGenericAutoCommit = config.getBoolean("server.processes.genericAvro.autoCommit")
  lazy val avroGenericCommitInterval = config.getInt("server.processes.genericAvro.commitInterval")
  lazy val avroGenericSessionTimeout = config.getInt("server.processes.genericAvro.sessionTimeout")
  lazy val avroGenericConsumerTimeout = config.getInt("server.processes.genericAvro.consumerTimeout")
  lazy val avroGenericConsumerPollTimeout = config.getInt("server.processes.genericAvro.consumerPollTimeout")

  //schemaRegistry
  lazy val schemaRegistryProcessTopic = config.getString("server.processes.schemaRegistry.topic")
  lazy val schemaRegistryGroup = config.getString("server.processes.schemaRegistry.group")
  lazy val schemaRegistryAutoCommit = config.getBoolean("server.processes.schemaRegistry.autoCommit")
  lazy val schemaRegistryCommitInterval = config.getInt("server.processes.schemaRegistry.commitInterval")
  lazy val schemaRegistrySessionTimeout = config.getInt("server.processes.schemaRegistry.sessionTimeout")
  lazy val schemaRegistryConsumerTimeout = config.getInt("server.processes.schemaRegistry.consumerTimeout")
  lazy val schemaRegistryConsumerPollTimeout = config.getInt("server.processes.schemaRegistry.consumerPollTimeout")

  //streams
  lazy val inputStreamsTopic = config.getString("server.processes.streams.inTopic")
  lazy val outputStreamsTopic = config.getString("server.processes.streams.outTopic")
  lazy val configStreamsTopic = config.getString("server.processes.streams.configTopic")

  //redis
  lazy val redisServer: String = config.getString("server.redis.servers")
  lazy val redisPort: Int = config.getInt("server.redis.port")
  lazy val redisThemeQueue = config.getString("server.processes.redisQueue.themeQueue")
}
