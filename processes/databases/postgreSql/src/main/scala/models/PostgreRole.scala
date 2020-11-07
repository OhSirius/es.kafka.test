package com.es.kafkatest.processes.database.postgreSql.models

import com.easysales.kafkatest.databases.common.models.Role
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._

//CREATE TABLE Role(
//	ID SERIAL PRIMARY KEY,
//	Parent INTEGER NOT NULL,
//	Name VARCHAR(128),
//	Comment VARCHAR(512)
// )

class PostgreRoles(tag:Tag) extends Table[Role](tag, "role"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def parent = column[Int]("parentid")
  def name = column[String]("name")
  def comment = column[String]("comment")
  def  * = (id, parent, name, comment)<> (Role.tupled, Role.unapply)
}

object  PostgreRoles {
  lazy val roles = TableQuery[PostgreRoles]
}