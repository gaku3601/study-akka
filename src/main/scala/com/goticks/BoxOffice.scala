package com.goticks

import akka.actor.Actor

object BoxOffice {

  def name = "boxOffice"

  // request
  case class CreateEvent(name: String, tickets: Int)

  // response
  sealed trait EventResponse // 抽象化するために使用
  case class EventCreated(event: Event) extends EventResponse

  // entity
  case class Event(name: String, tickets: Int)

}

class BoxOffice() extends Actor {

  import BoxOffice._

  override def receive: Receive = {
    case CreateEvent(name, tickets) =>
      sender() ! EventCreated(Event(name, tickets))
  }
}

