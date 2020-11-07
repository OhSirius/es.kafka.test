package com.es.kafkatest.inf.common.extensions

import java.io.OutputStream

import scala.util.{Failure, Success, Try}

//https://www.phdata.io/try-with-resources-in-scala/
//https://www.scala-lang.org/api/2.9.3/scala/util/Try.html
//https://docs.scala-lang.org/tour/currying.html
//https://www.scala-lang.org/old/node/6759
object ResourcesExtensions {
  def using[A, B](resource: A)(dispose: A => Unit)(action: A => B): B = {
    try {
      //Success(action(resource))
      action(resource)
    }
    //catch {
    //  case e: Exception => Failure(e)
    //}
    finally {
      try {
        if (resource != null) {
          dispose(resource)
        }
      } catch {
        case e: Exception => println(e) // should be logged
      }
    }
  }

  def usingStream[A<:OutputStream, B](resource:A)(action:A=>B):B=using(resource)(_.close())(action)

  def usingResource[A<:{ def close():Unit}, B](resource:A)(action:A=>B) = using(resource)(_.close())(action)
}
