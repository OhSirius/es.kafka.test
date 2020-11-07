package com.es.kafkatest.processes.database.postgreSql.repositories

import com.easysales.kafkatest.databases.common.Props
import com.easysales.kafkatest.databases.common.models.User
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
import com.es.kafkatest.inf.common.extensions.StringExtensions._

import com.google.inject.Inject
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DirectSqlPostgreUserRepository @Inject() (private  val props:Props)  extends IUserRepository{

  private lazy val db = DatabaseConfig.forConfig[JdbcProfile](props.path, props.config).db


  override def selectCount(): Future[Int] = {
    val action = sql"""SELECT COUNT(*)   FROM postgreuser""".as[Int]
    for {
      data <- db.run(action)
    } yield data.head
  }

  override def selectRolesCount(): Future[Int] = {
    val action = sql"""SELECT COUNT(*)   FROM role""".as[Int]
    for {
      data <- db.run(action)
    } yield data.head

  }

    //http://qaru.site/questions/31040/postgresql-function-for-last-inserted-id
  override def save(name:String, fullName:String, role: Int): Future[Int] = {
    assert(role>0,"Не определена роль")
    assert(!(name.isNullOrEmpty()), "Не задано имя")

    val action = sql"""INSERT INTO postgreuser (name, fullName, role) Values(${name}, ${fullName}, ${role}) RETURNING id;""".as[Int]
    for {
      data <- db.run(action)
    } yield data.head
  }

  override def delete(userId: Int): Future[Int] = {
    assert(userId>0,"Не задан Id пользователя")

    val action = sqlu"DELETE FROM postgreuser WHERE id = ${userId}"
    for {
      data <- db.run(action)
    } yield data
  }

  override def find(userId: Int): Future[Option[User]] = {
    assert(userId > 0, "Не задан Id пользователя")

    val action = sql"""SELECT * FROM PostgreUser WHERE id = ${userId}""".as[(Int, String, String, Int)]
    for {
      data <- db.run(action)
    } yield {

      for { (id, name, fullName, role) <- data.headOption} yield User(id, name, fullName, role)

    }
  }


  override def findByRole(roleId:Int):Future[Seq[User]] ={
    assert(roleId>0,"Не определена роль")

    val action = sql"""SELECT * FROM postgreuser WHERE role = ${roleId}""".as[(Int, String, String,Int)]
    for {
      data <- db.run(action)

    } yield data.map(p=>User(p._1, p._2, p._3, p._4))
  }

  override def deleteAll(): Future[Int] = {
    val action = sqlu"DELETE FROM postgreuser"
    for {
      data <- db.run(action)
    } yield data
  }
}
