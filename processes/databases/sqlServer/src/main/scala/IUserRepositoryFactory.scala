package com.es.kafkatest.processes.database.sqlServer

import com.es.kafkatest.processes.database.sqlServer.RepTypes.RepTypes
import repositories.IUserRepository

trait IUserRepositoryFactory {
  def create(repTypes: RepTypes):IUserRepository
}
