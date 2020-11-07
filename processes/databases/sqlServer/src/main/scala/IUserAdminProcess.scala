package com.es.kafkatest.processes.database.sqlServer

import scala.concurrent.Future

trait IUserAdminProcess {
   def findUsers():Future[Boolean]
   def createUsers():Future[Boolean]
   def deleteUsers():Future[Boolean]
}
