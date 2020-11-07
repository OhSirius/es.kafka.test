package com.es.kafkatest.processes.database.sqlServer.repositories

import com.easysales.kafkatest.databases.common.models.User

import scala.concurrent.Future

//Пример работы с Sql-сервером из Scala
//на основе https://github.com/sachabarber/ScalaSlickTest
trait IUserRepository {
 def selectCount():Future[Int]
 def selectRolesCount():Future[Int]
 def save(name:String, fullName:String, role: Int): Future[Int]
 def delete(userId:Int):Future[Int]
 def deleteAll():Future[Int]
 def find(userId:Int):Future[Option[User]]
 def findByRole(roleId:Int):Future[Seq[User]]
}
