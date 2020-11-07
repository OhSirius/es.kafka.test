package com.es.kafkatest.processes.redis

import scala.concurrent.Future

trait IRedisProcess [Tkey,TValue]{
    def put(user: TValue) : Future[Boolean]
    def pull(key: Tkey): Future[TValue]
}
