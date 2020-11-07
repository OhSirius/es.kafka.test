package com.es.kafkatest.processes.redisconsumer.process


import com.es.kafkatest.processes.redisconsumer.repositories.IRedisRepository
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory



class RedisConsumerProcess @Inject()(private val _repository : IRedisRepository[User])
  extends IRedisConsumerProcess
{
  private lazy val logger = Logger(LoggerFactory.getLogger(this.getClass))
  var shouldRun : Boolean = true

  override def start(): Unit = {

    try {
      while (shouldRun) {
        Runtime.getRuntime.addShutdownHook(new Thread(() => close()))
        var users = _repository.receive()
        if(users != null && !users.isEmpty) {
          println("Получен список пользователей", users)
          logger.info(s"Получен список пользователей ${users}")
        }
        _repository.commit()
      }

    }
    catch {
        case ex: Exception => ex.printStackTrace()
        println("Получена ошибка ")
    }
  }
  def close(): Unit = shouldRun = false
}
