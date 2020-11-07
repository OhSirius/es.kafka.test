package com.es.kafkatest.inf.common.extensions

import scala.reflect._
import scala.reflect.runtime.universe._
import scala.language.implicitConversions

//https://stackoverflow.com/questions/20684572/scala-convert-map-to-case-class
//https://docs.scala-lang.org/overviews/reflection/typetags-manifests.html
//https://scalerablog.wordpress.com/2015/12/21/classtag-class-and-war-stories/
//https://eax.me/scala-typetags/
//https://docs.scala-lang.org/overviews/reflection/typetags-manifests.html
//https://stackoverflow.com/questions/8000903/what-are-all-the-uses-of-an-underscore-in-scala - использование _
//https://docs.scala-lang.org/tutorials/FAQ/finding-symbols.html - ключевые слова
//https://stackoverflow.com/questions/39088871/dynamically-create-case-class
//https://stackoverflow.com/questions/27473440/scala-get-constructor-parameters-at-runtime
//https://stackoverflow.com/questions/4981689/get-item-in-the-list-in-scala
//https://docs.scala-lang.org/tour/implicit-conversions.html
//https://alvinalexander.com/scala/how-to-cast-objects-class-instance-in-scala-asinstanceof
object Activator {
  implicit class ClassMapConverter(val m: Map[String,_]) {//extends AnyVal {
    def convert[T:TypeTag:ClassTag]():T = {
      val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
      val classTest = typeOf[T].typeSymbol.asClass
      val classMirror = rm.reflectClass(classTest)
      val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asTerm.alternatives.head.asMethod//.asMethod
      val constructorMirror = classMirror.reflectConstructor(constructor)

      val constructorArgs = constructor.paramLists.flatten.map( (param: Symbol) => {
        val paramName = param.name.toString
        if(param.typeSignature <:< typeOf[Option[Any]])
           m.get(paramName)//.asInstanceOf[Option[String]]
        //else if(paramName == "id")
        //  m.get(paramName).getOrElse(throw new IllegalArgumentException("Map is missing required parameter named " + paramName)).asInstanceOf[Int]
        else
          m.get(paramName).getOrElse(throw new IllegalArgumentException("В структуре данных отсутствует параметр " + paramName))//.asInstanceOf[String]

      })

      constructorMirror(constructorArgs:_*).asInstanceOf[T]
    }
    }

  implicit class MapMessage(val message: Product) extends AnyVal {
    def convertToMap():Map[String, Any] = {
      var mapMessage: Map[String, Any] = Map.empty[String, Any]
      for ((key, value) <- message.getClass.getDeclaredFields.map(_.getName).zip(message.productIterator.to).toMap) {
        //if(message.schema.getField(key).get.get. <:<  classTag(Option[Any]))

        if (value.isInstanceOf[Option[Any]]) {
          mapMessage += (key -> value.asInstanceOf[Option[Any]].orNull)
        }
        else
          mapMessage += (key -> value)

      }
      mapMessage
    }
  }

  //object Activator  {
    def instanceOf[T:TypeTag:ClassTag]():T = {
      val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
      val classTest = typeOf[T].typeSymbol.asClass
      val classMirror = rm.reflectClass(classTest)
      //val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
      //val constructorMirror = classMirror.reflectConstructor(constructor)
      val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asTerm.alternatives.find(c=>c.asMethod.paramLists.flatten.isEmpty).get.asMethod
      val constructorMirror = classMirror.reflectConstructor(constructor)


      constructorMirror().asInstanceOf[T]

//      val mirror = ru.runtimeMirror(getClass.getClassLoader)
//      val clsSym = tpe.typeSymbol.asClass
//      val clsMirror = mirror.reflectClass(clsSym)
//      val ctorSym = tpe.decl(ru.termNames.CONSTRUCTOR).asMethod
//      val ctorMirror = clsMirror.reflectConstructor(ctorSym)
//      val instance = ctorMirror()
    //}
  }

}


//class TestBounds[U, T <: U with I] {}
