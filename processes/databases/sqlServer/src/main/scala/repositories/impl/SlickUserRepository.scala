package com.es.kafkatest.processes.database.sqlServer.repositories.impl

import com.easysales.kafkatest.databases.common.Props
import com.easysales.kafkatest.databases.common.models.User
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.es.kafkatest.processes.databases.sqlServer.models.{Roles, Users}
import com.google.inject.Inject
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcProfile, TransactionIsolation}
import slick.jdbc.SQLServerProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//Примеры
//https://powerspace.tech/using-slick-in-production-dbfcbe29545c
//https://stackoverflow.com/questions/34142408/configure-hikaricp-in-slick-3
//https://sysgears.com/notes/how-to-update-entire-database-record-using-slick/ - обновляем одну запись
//https://sysgears.com/articles/pagination-with-slick-how-to-properly-build-select-queries/ - делаем постраничное разбиение
//https://sysgears.com/articles/building-rest-service-with-scala/ - построение API
//https://sysgears.com/articles/managing-configuration-of-distributed-system-with-apache-zookeeper-2/ - строим конфиг для Kafka
//https://sysgears.com/articles/how-to-build-a-simple-mongodb-dao-in-scala-using-salatdao/ - работаем с Mongo
//https://sysgears.com/notes/table-existence-check-using-slick/ - проверяем что таблица есть
//http://slick.lightbend.com/doc/3.2.0/orm-to-slick.html - навигационные поля для Scala
//https://blog.knoldus.com/best-practices-for-using-slick-on-production/ - примеры с навигац. полями в продакшене
//https://powerspace.tech/using-slick-in-production-dbfcbe29545c - примеры join-ов
class SlickUserRepository @Inject() (private  val props:Props) extends IUserRepository{
  private lazy val db = DatabaseConfig.forConfig[JdbcProfile](props.path, props.config).db//DatabaseConfig[JdbcProfile] //SQLServerProfile

  override def selectCount(): Future[Int] = {
    implicit val session: Session = db.createSession()
    //session.withTr
    val action = Users.users.length //returning Users.users.countDistinct
    for {
      //data <- db.run(action.result.withTransactionIsolation(TransactionIsolation.ReadUncommitted))
      data <- db.run(action.result)
    } yield data
  }

  override def selectRolesCount(): Future[Int] = {
    val action = Roles.roles.length
    for {
      data <- db.run(action.result)
    } yield data
  }

  override def save(name: String, fullName: String, role: Int): Future[Int] = {
    val action =(Users.users returning Users.users.map(_.id)) +=
      User(-1, name, fullName, role)
    for {
      data <- db.run(action)
    } yield data
  }

  override def delete(userId: Int): Future[Int] = {
    val  action = Users.users.filter(_.id === userId).delete
    for {
      data <- db.run(action)
    } yield data
  }

  override def find(userId: Int): Future[Option[User]] = {
    val action = for { u <- Users.users if u.id === userId } yield u
    for {
      data <- db.run(action.result.headOption)
    } yield data //data.getOrElse(User(0,null,null,0))
  }

  override def findByRole(roleId: Int): Future[Seq[User]] = {
    val action = for { u <- Users.users if u.roleId === roleId } yield u
    for {
      data <- db.run(action.result)
    } yield data
  }

  override def deleteAll(): Future[Int] = {
    val action = Users.users.delete
    for {
      data <- db.run(action)
    } yield data

  }
}
