package com.es.kafkatest.processes.redisconsumer.serializer

import java.io.{ByteArrayInputStream, ObjectInputStream}

import com.es.kafkatest.inf.common.extensions.ResourcesExtensions.usingResource
import com.es.kafkatest.processes.schemaregistry.models.User
import com.google.inject.Inject

class Serializer[TValue] @Inject()extends ISerializer[TValue] {
  override def deserialize(bytes: Array[Byte]): TValue =
    usingResource(new ByteArrayInputStream(bytes)) (_ => {
      val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
      val value = ois.readObject
      ois.close()
      value.asInstanceOf[TValue]
    })
}
