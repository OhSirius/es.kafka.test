package com.es.kafkatest.processes.databases.sqlServer.models

import com.easysales.kafkatest.databases.common.models.Role
import slick.lifted.Tag
import slick.jdbc.SQLServerProfile.api._

//CREATE TABLE [dbo].[LinuxRole](
//	[ID] [int] IDENTITY(1,1) NOT NULL,
//	[Parent] [int] NOT NULL,
//	[Name] [varchar](128) NULL,
//	[Comment] [varchar](512) NULL
//) ON [PRIMARY]


class Roles(tag:Tag) extends Table[Role](tag, "LinuxRole"){
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def parent = column[Int]("ParentID")
  def name = column[String]("Name")
  def comment = column[String]("Comment")
  def  * = (id, parent, name, comment)<> (Role.tupled, Role.unapply)
}

object  Roles {
  lazy val roles = TableQuery[Roles]
}