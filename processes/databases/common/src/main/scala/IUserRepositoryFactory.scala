package com.easysales.kafkatest.databases.common

import com.easysales.kafkatest.databases.common.RepTypes.RepTypes
import com.easysales.kafkatest.databases.common.repositories.IUserRepository


trait IUserRepositoryFactory {
  def create(repTypes: RepTypes):IUserRepository
}
