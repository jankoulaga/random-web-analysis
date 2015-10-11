package com.example

import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal


class RequestActor(httpClient: HttpClient) extends Actor with ActorLogging {

  def receive = {
    case BulkControllerActor.Get(text) =>
      val receiver = sender()
      httpClient.get(text).map(receiver ! _).recover {
        case NonFatal(x) => receiver ! ResponseWithError(x, "")
      }
  }
}

object RequestActor {
  def props(httpClient: HttpClient) = Props(new RequestActor(httpClient))
}
