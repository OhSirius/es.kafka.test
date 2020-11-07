package com.es.kafkatest.processes.redisconsumer.process

import scala.concurrent.Future

trait IRedisConsumerProcess {
  def start():Unit
}
