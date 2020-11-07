package com.es.kafkatest.processes.database.postgreSql.bootstrap

import com.easysales.kafkatest.databases.common.{IUserAdminProcess, IUserRepositoryFactory, Props}
import com.easysales.kafkatest.databases.common.RepTypes.RepTypes
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.processes.database.postgreSql.impl.{PostgreUserAdminProcess, PostgreUserRepositoryFactory}
import com.es.kafkatest.processes.database.postgreSql.repositories.{DirectSqlPostgreUserRepository, SlickPostgreUserRepository}
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule

class PosgreSqlModule(private val props:Props, private val repType: RepTypes) extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  override def configure() = {
    //bindFactory[IConsumer[TMessage], Consumer[TMessage], IConsumerFactory[TMessage]]
    bind[Props].toInstance(props)
    bind[RepTypes].toInstance(repType)
    bind[IUserRepository].annotatedWithName("DirectSql").to[DirectSqlPostgreUserRepository]
    bind[IUserRepository].annotatedWithName("Slick").to[SlickPostgreUserRepository]
    bind[IUserRepositoryFactory].to[PostgreUserRepositoryFactory]
    bind[IUserAdminProcess].to[PostgreUserAdminProcess]
  }
}

class PosgreSqlConfiguration(private val props:Props, private val repType: RepTypes) {
  def configure(): IUserAdminProcess = {
    val injector = Guice.createInjector(new PosgreSqlModule(props, repType))
    val producerFactory = injector.instance[IUserAdminProcess]
    producerFactory
  }
}