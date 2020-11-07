package com.es.kafkatest.processes.redis.serializer

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.es.kafkatest.inf.common.extensions.ResourcesExtensions.{usingResource, usingStream}
import com.google.inject.Inject

class Serializer[TValue] @Inject()extends ISerializer[TValue] {

  override def serialize(value: TValue ):  Array[Byte]=
    usingStream(new ByteArrayOutputStream())(out => {
      val oos = new ObjectOutputStream(out)
      oos.writeObject(value)
      oos.close()
      out.toByteArray
    })
}
