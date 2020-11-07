package com.easysales.kafkatest.databases.common

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}

case class Props(path:String, config:Config)