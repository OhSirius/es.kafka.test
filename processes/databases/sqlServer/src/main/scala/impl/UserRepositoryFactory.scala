package com.es.kafkatest.processes.database.sqlServer.impl

import com.easysales.kafkatest.databases.common.{IUserRepositoryFactory, RepTypes}
import com.easysales.kafkatest.databases.common.RepTypes.RepTypes
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.google.inject.Inject
import com.google.inject.name.Names
import com.google.inject.name._

//import com.google.inject.name.Names

//https://github.com/codingwell/scala-guice
//@Names("DirectSql")
class UserRepositoryFactory @Inject() ( @Named("DirectSql")  private val directSqlUserRep: IUserRepository, @Named("Slick") private  val slickUserRep:IUserRepository)  extends IUserRepositoryFactory{
  override def create(repTypes: RepTypes): IUserRepository = {
    repTypes match {
      case RepTypes.DirectSql=> directSqlUserRep
      case  RepTypes.Slick => slickUserRep
      case _ => throw  new UnsupportedOperationException(s"Не определен репозиторий для типа $repTypes")
    }
  }
}
