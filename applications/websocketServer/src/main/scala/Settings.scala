
package com.es.kafkatest.app.websocketserver

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}


case class Settings() {

  private var config: Config = null

  def initialize():Unit={
    config = ConfigFactory.load()
  }

  // lazy val websocketServerHost: String = config.getString("websocket.server.host")
  lazy val websocketServerPort: Int = config.getInt("websocket.server.port")

}