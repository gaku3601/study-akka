package com.goticks

import com.goticks.BoxOffice.Event
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class EventDescription(tickets: Int) // ticketCreate時に受け付けるJson定義

trait EventMarshalling extends DefaultJsonProtocol {
  implicit val eventDescriptionFormat: RootJsonFormat[EventDescription] = jsonFormat1(EventDescription)
  implicit val eventFormat: RootJsonFormat[Event] = jsonFormat2(Event) // Eventは引数が2つなのでjsonFormat2を選択
}
