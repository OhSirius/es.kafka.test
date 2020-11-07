package com.es.kafkatest.processes.redis.repositories

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.es.kafkatest.inf.common.extensions.ResourcesExtensions._
import com.es.kafkatest.processes.redis.bootstrap.Props
import com.es.kafkatest.processes.redis.redisqueue.bootstrap.QueueProps
import com.es.kafkatest.processes.redis.serializer.ISerializer
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.Inject
import com.redis.RedisClient
import com.redis.serialization.Parse.Implicits._

class RedisClientRepository @Inject()(private  val prop: Props)
  extends IRedisClientRepository[Int,User]
{

  lazy val redis = new RedisClient(prop.serverUrl,prop.port)


  override def putRedis(user: User): Boolean = {
    redis.set(user.id, serialize(user))

  }

  override def getRedis(key: Int): User = {
    var user = redis.get[Array[Byte]](key)
    user match {
      case Some(user) => deserialize(user)
      case None => User(0, "")
    }
  }

  private def serialize(message:User): Array[Byte] = {

    usingStream(new ByteArrayOutputStream())(out => {
      val oos = new ObjectOutputStream(out)
      oos.writeObject(message)
      oos.close()
      out.toByteArray
    })
  }

  def deserialize(bytes: Array[Byte]): User = {
    usingResource(new ByteArrayInputStream(bytes)) (out => {
      val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
      val value = ois.readObject
      ois.close()
      value.asInstanceOf[User]
    })
  }


}


class RedisQueueRepository[TValue] @Inject() (private val prop: QueueProps,
                                              private val _serializer : ISerializer[TValue])
  extends IRedisQueueRepository[TValue]
{
  lazy val redis = new RedisClient(prop.serverUrl,prop.port)

  override def push(user: TValue): Unit = {
    try
      {
        if(user != null)
          {
            redis.lpush(prop.themeQueue, _serializer.serialize(user))
          }
      }
    catch
      {
        case ex: Exception =>
          println(ex.printStackTrace().toString)
          ex.printStackTrace()
      }

  }
}
