package com.example

import java.util.UUID

import akka.actor._

class BulkControllerActor(limit: Int, url: String) extends Actor with ActorLogging with HttpClientProvider {

  import BulkControllerActor._

  var counter = 0
  var resultList: List[ClientResponse] = List()
  var receiver: Option[ActorRef] = None
  lazy val asyncClient: HttpClient = AsyncClient 

  def requestActor = context.actorOf(RequestActor.props(asyncClient), s"requestActor-${UUID.randomUUID().toString}")

  def receive = {
    case Initialize =>
      receiver = Some(sender())
      0 to limit foreach (_ => requestActor ! Get(url))
    case message: ClientResponse =>
      counter += 1
      processMessage(message)
      if (counter == limit) sendBackData
    case Shutdown =>
      context.system.shutdown()

  }

  def sendBackData = receiver.foreach(_ ! resultList)

  def processMessage(message: ClientResponse) = resultList = resultList :+ message

}

object BulkControllerActor {
  def props(limit: Int, url: String) = Props(new BulkControllerActor(limit, url))
  case object Initialize
  case object Shutdown
  case class Get(url: String)

}