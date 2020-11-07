package com.es.kafkatest.processes.database.postgreSql.impl

import com.easysales.kafkatest.databases.common.{IUserRepositoryFactory, RepTypes}
import com.easysales.kafkatest.databases.common.RepTypes.RepTypes
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.google.inject.Inject
import com.google.inject.name.Named

class PostgreUserRepositoryFactory @Inject() ( @Named("DirectSql")  private val directSqlPostgreUserRep: IUserRepository, @Named("Slick") private  val slickPostgreUserRep:IUserRepository)  extends IUserRepositoryFactory{
  override def create(repTypes: RepTypes): IUserRepository = {
    repTypes match {
      case RepTypes.DirectSql=> directSqlPostgreUserRep
      case  RepTypes.Slick => slickPostgreUserRep
      case _ => throw  new UnsupportedOperationException(s"Не определен репозиторий для типа $repTypes")
    }
  }
}
