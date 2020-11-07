
package com.es.kafkatest.app.websocketserver

import java.util.{Timer, TimerTask}

import akka.NotUsed
import akka.actor.ActorRef
import com.es.kafkatest.app.websocketserver.AkkaHttpImplicits.{actorSystemExecutionContext, actorMaterializer, actorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, DateTime, HttpEntity}
import akka.http.scaladsl.server.Directives

import scala.io.StdIn
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import org.reactivestreams.Publisher
import spray.json.{DefaultJsonProtocol}

import scala.concurrent.duration._
import scala.concurrent.Future


final case class WebInput(str1: String, str2: String)

final case class WebResult(str: String, strlen: Int)




object WebSocketServerApp extends App with Directives with SprayJsonSupport with DefaultJsonProtocol  {

  //  "with Directives" provides "path", "get", "post", ...

  override def main(args:Array[String]):Unit = {

    implicit val webinputFormat = jsonFormat2(WebInput)
    implicit val webresultFormat = jsonFormat2(WebResult)

    // для тестов WebSocket лучше использовать клиенты, которые отображают всю историю сообщений с сервера
    // например https://chrome.google.com/webstore/detail/browser-websocket-client/mdmlhchldhfnfnkfmljgeinlffmdgkjo

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    // TODO: решить проблему с некорректным (или слишком долгим?) закрытием websocket соединения

    val websocketEchoFlow: Flow[Message, Message, Any] =
      Flow[Message].mapConcat {
        case tm: TextMessage =>
          // прием текстовых сообщений. отправляем их обратно клиенту
          TextMessage(Source.single("Message is: ") ++ tm.textStream ) :: Nil

        case bm: BinaryMessage =>
          // прием бинарных сообщений. игнорируем их и помечаем поток как прочитанный
          // ignore binary messages but drain content to avoid the stream being clogged
          println("binary message received. ignoring")
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }

    //------------------------------------------------------------------------------------------------------------------

    // генерирует сообщение со строковой константой раз в 3,1 секунды.
    val tickConstantSource = Source.tick[String](1.second, (3.1).second, "Constant 3.1 seconds")

    //------------------------------------------------------------------------------------------------------------------

    // генерирует сообщение с результатом вызова функции раз в секунду
    val tickFunctionSource = Source.tick(1.second, 1.second, NotUsed).map(_ => s"Variable datetime. now is: ${DateTime.now.toString()}")

    //------------------------------------------------------------------------------------------------------------------

    // updatableActorRef позволяет отправлять данные во Flow из любого места...
    // https://stackoverflow.com/a/49738669
    // TODO: решить проблему с закрытием publisher при отключении от него всех websocket клиентов
    val (updatableActorRef: ActorRef, publisher: Publisher[String]) =
      Source
        .actorRef[String](bufferSize = 10, OverflowStrategy.dropHead)
        .toMat(Sink.asPublisher(true))(Keep.both)
        .run()

    val updatableSource = Source.fromPublisher(publisher)

    // ... в данном случае - по таймеру отправляется некое сообщение.
    val updatableSourceTask = new TimerTask {
      def run(): Unit = updatableActorRef ! s"datetime.now from updatableSource: ${DateTime.now.toString()}"
    }

    (new Timer).schedule(updatableSourceTask, 0, 5200)

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    var websocketNotificatorFlow: Flow[Message, Message, Any] = {
      Flow[Message].collect {
        case tm: TextMessage ⇒ tm.textStream
      }
      .mapAsync(1)(in ⇒ in.runFold("")(_ + _).flatMap(in ⇒ Future(in)))
      .collect {
        case str => s"from client: $str"
      }
      .merge(tickConstantSource)
      .merge(tickFunctionSource)
      .merge(updatableSource)
      .mapAsync(1)(out ⇒ Future(TextMessage(out)))
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------

    val route =
      path("random") {
        get {
          println(s"random")
          // /random , возвращает случайное число. метод - GET
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, math.random().toString))
        }
      } ~
      path("testpost") {
        post {
          println(s"testpost")
          // /testpost , json сериализация объектов с использованием implicit указанных выше. метод - POST
          // пример запроса: { "str1": "qwe", "str2": "asd" }
          // пример ответа: { "str": "dsaewq", "strlen": 6 }
          entity(as[WebInput]) { wr =>
            println(s"Server saw wr : $wr")
            val str = wr.str1 + wr.str2
            complete(WebResult(str.reverse, str.length))
          }
        }
      } ~
      path("wsEcho") {
        println(s"wsEcho")
        // /wsEcho , websocket эхо-сервис, возвращает переданную строку
        handleWebSocketMessages(websocketEchoFlow)
      } ~
      path("wsNotificator") {
        println(s"wsNotificator")
        // /wsNotificator, websocket сервис уведомлений, сервер периодически отсылает клиенту данные
        handleWebSocketMessages(websocketNotificatorFlow)
      }

    //------------------------------------------------------------------------------------------------------------------

    val settings = Settings()
    settings.initialize()

    // адрес должен быть "0.0.0.0", это важно для работы из докер контейнера
    val (host, port) = ("0.0.0.0", settings.websocketServerPort)

    val bindingFuture = Http().bindAndHandle(route, host, port)

    bindingFuture.onFailure {
      case ex: Exception =>
        println(s"Failed to bind to $host:$port! Exception: $ex")
    }

    println(s"WebSocket server online at http://$host:$port/")

    //println(s"WebSocket server online at http://$host:$port/\nPress RETURN to stop...")
    //StdIn.readLine()
    //
    // bindingFuture
    //   .flatMap(_.unbind())
    //   .onComplete(_ ⇒ actorSystem.terminate())

    //------------------------------------------------------------------------------------------------------------------


  }

}
