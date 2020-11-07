package com.es.kafkatest.processes.database.postgreSql.models

import com.easysales.kafkatest.databases.common.models.{Role, User}
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._

//CREATE TABLE PostgreUser(
//	ID SERIAL PRIMARY KEY,
//	Name VARCHAR(256) NOT NULL,
//	FullName VARCHAR(512),
//	Role INTEGER NOT NULL
// )


class PostgreUsers(tag:Tag) extends Table[User](tag, "postgreuser"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def fullName = column[String]("fullname")
  def roleId = column[Int]("role")
  def  * = (id, name, fullName, roleId)<> (User.tupled, User.unapply)
  def role = foreignKey("Role_FK", roleId, PostgreRoles.roles)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
}

object  PostgreUsers{
  lazy val users = TableQuery[PostgreUsers]
}

