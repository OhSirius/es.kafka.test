package com.easysales.kafkatest.databases.common

import scala.concurrent.Future

trait IUserAdminProcess {
   def findUsers():Future[Boolean]
   def createUsers():Future[Boolean]
   def deleteUsers():Future[Boolean]
}
