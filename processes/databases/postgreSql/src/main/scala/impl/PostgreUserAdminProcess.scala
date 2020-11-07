package com.es.kafkatest.processes.database.postgreSql.impl

import com.easysales.kafkatest.databases.common.{IUserAdminProcess, IUserRepositoryFactory}
import com.easysales.kafkatest.databases.common.RepTypes.RepTypes
import com.google.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PostgreUserAdminProcess @Inject() (private  val repFactory: IUserRepositoryFactory, private val repType: RepTypes) extends IUserAdminProcess{
  override def findUsers(): Future[Boolean] = {
    assert(repFactory!= null, "Не определена фабрика репозиториев")

    val rep = repFactory.create(repType)
    for {
      usersCount <- rep.selectCount()
      rolesCount <- rep.selectRolesCount()
    } yield {
      println(s"Найдено ${usersCount} пользователей и ${rolesCount} ролей")
      true
    }
  }

  override def createUsers(): Future[Boolean] = {
    assert(repFactory!= null, "Не определена фабрика репозиториев")

    val rep = repFactory.create(repType)
    for{
      user1 <-rep.save("sirius", "Павлычев", 1)
      user2 <- rep.save("kapunkin","Капункин", 1)
      users <-rep.findByRole(1)
    } yield{
      if(users.isEmpty)
        println("Пользователи не созданы")
      else
        users.foreach(u=>println(s"Создан пользователь: ${u}"))
      true
    }
  }

  override def deleteUsers(): Future[Boolean] = {
    assert(repFactory!= null, "Не определена фабрика репозиториев")

    val rep = repFactory.create(repType)
    for{
      allCount<-rep.deleteAll()
      userId <-rep.save("borisov", "Борисов",1)
      user <-rep.find(userId)
      count<-rep.delete(userId)
      delUser<-rep.find(userId)
    } yield {
      println(s"Удалены все пользователи: ${allCount}")
      println(s"Создан пользователь Борисов c ID ${userId}")
      println(s"Удален пользователь с ID ${userId}")
      println(s"Пользоваль Борисов не найден: ${delUser==null || delUser.isEmpty}")
      true
    }

  }


}
