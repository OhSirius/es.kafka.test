package com.es.kafkatest.processes.database.sqlServer.repositories.impl

import com.easysales.kafkatest.databases.common.Props
import com.easysales.kafkatest.databases.common.models.User
import com.easysales.kafkatest.databases.common.repositories.IUserRepository
//import com.es.kafkatest.processes.database.sqlServer.models.User
import com.google.inject.Inject
import slick.basic.DatabaseConfig
import slick.jdbc.SQLServerProfile
import slick.jdbc.SQLServerProfile.api._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.es.kafkatest.inf.common.extensions.StringExtensions._

//import com.microsoft.sqlserver.jdbc//.SQLServerDriver

//import com.typesafe.slick.driver.ms.SQLServerDriver.api._

//http://slick.lightbend.com/doc/3.1.0/extensions.html
//https://blog.knoldus.com/using-microsoft-sql-server-with-scala-slick/
//http://slick.lightbend.com/news/2017/02/24/slick-3.2.0-released.html
//https://sachabarbs.wordpress.com/2015/12/01/scala-connecting-to-a-database/
//http://slick.lightbend.com/doc/3.2.0/sql.html
//https://github.com/knoldus/scala-slick-mssql
//https://stackoverflow.com/questions/42425501/slick-codegen-with-sqlserver-and-dbo-schema
//https://github.com/slick/slick-codegen-customization-example - генерация моделей из БД
class DirectSqlUserRepository @Inject() (private  val props:Props)  extends IUserRepository{
  //val db = Database.forConfig("dms")
  private lazy val db = DatabaseConfig.forConfig[JdbcProfile](props.path, props.config).db//DatabaseConfig[JdbcProfile] //SQLServerProfile
  //new SQLServerDriver()

  //class A{
  //  class B{}
  //}

  //def test(dd:DirectSqlUserRepository#A#B):Unit={}

  override def selectCount(): Future[Int] = {
    val action = sql"""Select count(*) as 'count'  From dbo.LinuxUser""".as[Int]
    for {
      data <- db.run(action)
    } yield data.head
  }

  override def selectRolesCount(): Future[Int] = {
    val action = sql"""Select count(*) as 'count'  From dbo.LinuxRole""".as[Int]
    for {
      data <- db.run(action)
    } yield data.head

  }

  //https://stackoverflow.com/questions/19146329/how-do-i-return-an-auto-generated-id-using-slick-plain-sql-with-sql-server
  override def save(name:String, fullName:String, role: Int): Future[Int] = {
    assert(role>0,"Не определена роль")
    assert(!name.isNullOrEmpty(), "Не задано имя")

    //val action = sqlu"Insert Into dbo.LinuxUser (Name, FullName, Role) Values(${name}, ${fullName}, ${role})"
    //val idAction= sql"""SELECT @@IDENTITY as ID""".as[Int]
    val action = sql"""SET NOCOUNT ON;Insert Into dbo.LinuxUser (Name, FullName, Role) Values(${name}, ${fullName}, ${role});SET NOCOUNT OFF;SELECT @@IDENTITY""".as[Int]
    for {
      data <- db.run(action)
      //id <- db.run(idAction)
    } yield data.head //id.head
  }

  override def delete(userId: Int): Future[Int] = {
    assert(userId>0,"Не задан Id пользователя")

    val action = sqlu"Delete dbo.LinuxUser Where ID = ${userId}"
    for {
      data <- db.run(action)
    } yield data
  }

  override def find(userId: Int): Future[Option[User]] = {
    assert(userId > 0, "Не задан Id пользователя")

    val action = sql"""Select * from dbo.LinuxUser Where ID = ${userId}""".as[(Int, String, String, Int)]
    for {
      data <- db.run(action)
      //head = Some(data.head)
      //if !data.isEmpty
    } yield {
      //val (id, name, fullName, role) = if(!data.isEmpty)  data.head else (0,"","",0)
      for { (id, name, fullName, role) <- data.headOption} yield User(id, name, fullName, role)
      //if(data.isEmpty)
      // return Future{ User(0,null,null,0)}

      //val (id, name, fullName, role) = data.head
      //User(id, name, fullName, role)
    }
  }


  override def findByRole(roleId:Int):Future[Seq[User]] ={
    assert(roleId>0,"Не определена роль")

    val action = sql"""Select * from dbo.LinuxUser Where Role = ${roleId}""".as[(Int, String, String,Int)]
    for {
      data <- db.run(action)
      //(id, name, fullName, role) <- data.head
      //if !data.isEmpty
    } yield data.map(p=>User(p._1, p._2, p._3, p._4))
  }

  override def deleteAll(): Future[Int] = {
    val action = sqlu"Delete dbo.LinuxUser"
    for {
      data <- db.run(action)
    } yield data
  }
}
