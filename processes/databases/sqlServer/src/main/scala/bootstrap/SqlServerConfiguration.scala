package com.es.kafkatest.processes.database.sqlServer.bootstrap


import com.easysales.kafkatest.databases.common.{IUserAdminProcess, IUserRepositoryFactory, Props}
import com.easysales.kafkatest.databases.common.RepTypes.RepTypes
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.es.kafkatest.inf.common.guice.AssistedInjectFactoryScalaModule
import com.es.kafkatest.processes.database.sqlServer.impl.{UserAdminProcess, UserRepositoryFactory}
import com.es.kafkatest.processes.database.sqlServer.repositories.impl.{DirectSqlUserRepository, SlickUserRepository}
import com.google.inject._
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule


//val db = Database.forConfig("dms")
//import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}
//case class Props(path:String, config:Config)

class SqlServerModule(private val props:Props, private val repType: RepTypes) extends AbstractModule with ScalaModule with AssistedInjectFactoryScalaModule[Binder] {
  override def configure() = {
    //bindFactory[IConsumer[TMessage], Consumer[TMessage], IConsumerFactory[TMessage]]
    bind[Props].toInstance(props)
    bind[RepTypes].toInstance(repType)
    bind[IUserRepository].annotatedWithName("DirectSql").to[DirectSqlUserRepository]
    bind[IUserRepository].annotatedWithName("Slick").to[SlickUserRepository]
    bind[IUserRepositoryFactory].to[UserRepositoryFactory]
    bind[IUserAdminProcess].to[UserAdminProcess]
  }
}

class SqlServerConfiguration(private val props:Props, private val repType: RepTypes) {
  def configure(): IUserAdminProcess = {
    val injector = Guice.createInjector(new SqlServerModule(props, repType))
    val producerFactory = injector.instance[IUserAdminProcess]
    producerFactory
  }
}