package com.es.kafkatest.processes.database.postgreSql.repositories

import com.easysales.kafkatest.databases.common.Props
import com.easysales.kafkatest.databases.common.models.User
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.es.kafkatest.processes.database.postgreSql.models.{PostgreRoles, PostgreUsers}
import com.google.inject.Inject
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SlickPostgreUserRepository @Inject() (private  val props:Props) extends IUserRepository{
  private lazy val db = DatabaseConfig.forConfig[JdbcProfile](props.path, props.config).db

  override def selectCount(): Future[Int] = {
    implicit val session: Session = db.createSession()

    val action = PostgreUsers.users.length
    for {

      data <- db.run(action.result)
    } yield data
  }

  override def selectRolesCount(): Future[Int] = {
    val action = PostgreRoles.roles.length
    for {
      data <- db.run(action.result)
    } yield data
  }

  override def save(name: String, fullName: String, role: Int): Future[Int] = {
    val action =(PostgreUsers.users returning PostgreUsers.users.map(_.id)) +=
      User(-1, name, fullName, role)
    for {
      data <- db.run(action)
    } yield data
  }

  override def delete(userId: Int): Future[Int] = {
    val  action = PostgreUsers.users.filter(_.id === userId).delete
    for {
      data <- db.run(action)
    } yield data
  }

  override def find(userId: Int): Future[Option[User]] = {
    val action = for { u <- PostgreUsers.users if u.id === userId } yield u
    for {
      data <- db.run(action.result.headOption)
    } yield data
  }

  override def findByRole(roleId: Int): Future[Seq[User]] = {
    val action = for { u <- PostgreUsers.users if u.roleId === roleId } yield u
    for {
      data <- db.run(action.result)
    } yield data
  }

  override def deleteAll(): Future[Int] = {
    val action = PostgreUsers.users.delete
    for {
      data <- db.run(action)
    } yield data

  }
}
