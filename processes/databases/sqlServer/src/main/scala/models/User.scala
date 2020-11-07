package com.es.kafkatest.processes.databases.sqlServer.models

import com.easysales.kafkatest.databases.common.models.User
import slick.lifted.Tag
//import slick.model.Table
//import com.typesafe.slick.driver.ms.SQLServerDriver.api._
import slick.jdbc.SQLServerProfile.api._

//CREATE TABLE [dbo].[LinuxUser](
//	[ID] [int] IDENTITY(1,1) NOT NULL,
//	[Name] [nvarchar](256) NOT NULL,
//	[FullName] [nvarchar](512) NULL,
//	[Role] [int] NOT NULL,
//) ON [PRIMARY] --TEXTIMAGE_ON [PRIMARY]
//case class User (id:Int, name:String, fullName:String, roleId:Int)//, role:Role)

class Users(tag:Tag) extends Table[User](tag, "LinuxUser"){
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("Name")
  def fullName = column[String]("FullName")
  def roleId = column[Int]("Role")
  def  * = (id, name, fullName, roleId)<> (User.tupled, User.unapply)
  def role = foreignKey("Role_FK", roleId, Roles.roles)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
}

object  Users{
  lazy val users = TableQuery[Users]
}
