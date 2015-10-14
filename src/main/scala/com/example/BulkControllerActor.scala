package com.example

import java.util.UUID

import akka.actor._

abstract class BulkController(limit: Int, url: String)extends Actor with ActorLogging with HttpClientProvider {
  import BulkControllerActor._

  val asyncClient: HttpClient
  def requestActor: ActorRef

  var counter = 0
  var resultList: List[ClientResponse] = List()
  var receiver: Option[ActorRef] = None

  def receive = {
    case Initialize =>
      receiver = Some(sender())
      0 to limit foreach (_ => requestActor ! Get(url))
    case withStatus: ResponseWithStatus =>
      counter += 1
      processMessage(withStatus)
      if (counter == limit) sendBackData
    case err : ResponseWithError =>
      requestActor ! Get(url)
    case Shutdown =>
      context.system.shutdown()

  }

  def sendBackData = receiver.foreach(_ ! resultList)

  def processMessage(message: ClientResponse) = resultList = resultList :+ message
}

class BulkControllerActor(limit: Int, url: String) extends BulkController(limit, url) with HttpClientComponent {
  def requestActor: ActorRef = context.actorOf(RequestActor.props(asyncClient), s"requestActor-${UUID.randomUUID().toString}")
}


object BulkControllerActor {
  def props(limit: Int, url: String) = Props(new BulkControllerActor(limit, url))
  case object Initialize
  case object Shutdown
  case class Get(url: String)
}