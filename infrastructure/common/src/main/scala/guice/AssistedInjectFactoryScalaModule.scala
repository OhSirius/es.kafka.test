package com.es.kafkatest.inf.common.guice

import com.google.inject.Binder
import net.codingwell.scalaguice.InternalModule
import com.google.inject.assistedinject.{FactoryModuleBuilder, FactoryProvider}
import net.codingwell.scalaguice.typeLiteral
import scala.reflect.runtime.universe.TypeTag

trait AssistedInjectFactoryScalaModule[B <: Binder] extends com.google.inject.Module {
  self: InternalModule[B] =>

  protected[this] def bindFactory[C: TypeTag, F: TypeTag]() {
    bindFactory[C, C, F]()
  }

  protected[this] def bindFactory[I: TypeTag, C <: I : TypeTag, F: TypeTag]() {
    binderAccess.install(new FactoryModuleBuilder()
      .implement(typeLiteral[I], typeLiteral[C])
      .build(typeLiteral[F]))
  }
}
