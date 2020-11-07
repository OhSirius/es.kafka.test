package com.es.kafkatest.processes.redisconsumer.repositories

import com.es.kafkatest.processes.redisconsumer.bootstrap.Props
import com.es.kafkatest.processes.redisconsumer.serializer.ISerializer
import com.google.inject.Inject
import com.redis.RedisClient
import com.redis.serialization.Parse.Implicits._

import scala.collection.mutable.ListBuffer



class RedisRepository[TValue]@Inject()(private val _serializer : ISerializer[TValue],
                              private  val prop: Props)
  extends IRedisRepository[TValue]
{
  val themeQueueTmp = prop.themeQueue.+("tmp")
  lazy val redis = new RedisClient(prop.serverUrl,prop.port)

  override def receive(): List[TValue] =
  {

    var count = redis.llen(prop.themeQueue)
    var countValue =  count match {
      case Some(cnt) => cnt.toInt
      case None => 0
    }

    for (i <- 1 to countValue)
    {
      redis.rpoplpush[Array[Byte]](prop.themeQueue, themeQueueTmp)
    }
    var list =  receiveQueue(themeQueueTmp)
    return list
  }

  //удаляет значения которые храняться во временном списке
  override def commit(): Unit = {
    redis.del(themeQueueTmp)//или использовать ltrim ??
  }

  private def receiveQueue(theme : String): List[TValue]  = {

    val listBuffer = new ListBuffer[TValue];
    var countValue = redis.llen(theme) match {
      case Some(count) => count
      case None => 0
    }
    //var list: ListBuffer[TValue] = new ListBuffer[TValue]
    if (countValue == 0) {
      return new ListBuffer[TValue].toList
    }

    var optionList = redis.lrange[Array[Byte]](theme, 0, countValue.asInstanceOf[Int])

    optionList match {
      case Some(list)=> {
        var usersByte = list.flatMap(u => u);
        usersByte.foreach(b => {
          var user = _serializer.deserialize(b)
          listBuffer += user
        })
        listBuffer.reverse.toList
      }
      case None => listBuffer.toList
    }
  }


}
