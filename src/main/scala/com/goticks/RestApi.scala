package com.goticks

import akka.actor._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  implicit val requestTimeout: Timeout = timeout

  implicit def executionContext: ExecutionContextExecutor = system.dispatcher

}

trait RestRoutes extends BoxOfficeApi with EventMarshalling {

  import akka.http.scaladsl.model.StatusCodes._

  def routes: Route = eventRoute

  def eventRoute: Route =
    pathPrefix("events" / Segment) { event =>
      pathEndOrSingleSlash {
        post {
          entity(as[EventDescription]) { ed =>
            onSuccess(createEvent(event, ed.tickets)) {
              complete(Created, event)
            }
          }
        }
      }
    }
}

trait BoxOfficeApi {
  implicit def executionContext: ExecutionContext

  def createEvent(event: String, nrOfTickets: Int): Future[Unit] = {
    Future {
      print(event)
      print(nrOfTickets)
    }
  }
}