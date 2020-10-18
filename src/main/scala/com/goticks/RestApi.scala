package com.goticks

import akka.actor._
//これがないとakka.http.scaladsl.unmarshalling.FromRequestUnmarshallerエラーになる
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout
import com.goticks.BoxOffice.{CreateEvent, EventCreated, EventResponse}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  implicit val requestTimeout: Timeout = timeout

  implicit def executionContext: ExecutionContextExecutor = system.dispatcher

  // Actorの生成
  def createBoxOffice(): ActorRef = system.actorOf(Props[BoxOffice], BoxOffice.name)
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
              case EventCreated(event) => complete(Created, event)
            }
          }
        }
      }
    }
}

trait BoxOfficeApi {
  implicit def executionContext: ExecutionContext

  implicit def requestTimeout: Timeout

  def createBoxOffice(): ActorRef

  lazy val boxOffice: ActorRef = createBoxOffice()

  def createEvent(event: String, nrOfTickets: Int): Future[EventResponse] = {
    boxOffice.ask(CreateEvent(event, nrOfTickets)).mapTo[EventResponse]
  }
}