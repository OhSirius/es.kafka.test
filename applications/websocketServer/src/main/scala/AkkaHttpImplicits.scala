
package com.es.kafkatest.app.websocketserver

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object AkkaHttpImplicits {
  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()
  implicit val actorSystemExecutionContext = actorSystem.dispatcher
}