package com.goticks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContextExecutor, Future}


object Main extends App with RequestTimeout {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val api = new RestApi(system, requestTimeout(config)).routes // RestApiの定義
  // implicit val materializer: ActorMaterializer.type = ActorMaterializer いらないかも
  val bindingFuture: Future[ServerBinding] = Http().newServerAt(host, port).bind(api) // サーバの起動
}

trait RequestTimeout {

  import scala.concurrent.duration.Duration

  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}