package com.example

import java.net.URL
import java.util.UUID

import akka.actor.ActorSystem
import akka.dispatch.sysmsg.Failed
import akka.pattern.ask
import akka.util.Timeout
import com.example.BulkControllerActor.Shutdown

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

trait WebProcessor {
  val results: Future[List[ClientResponse]]

  def printResults = getResultsAsString.map(line => println(line.mkString("\n")))

  def getResultsAsString = getSortedResults.map {
    _.flatMap {
      case ResponseWithStatus(code, siteUrl) =>
        val codeVal = if (code != 200) Some(code.toString) else None
        Some(List(Some(siteUrl), codeVal).flatten.mkString(", "))
      case _ => None
    }
  }

  def getSortedResults = results.map(sortedResults)

  private def sortedResults(results: List[ClientResponse]): List[ClientResponse] = results.sortBy(sortByDomain)

  private def sortByDomain: (ClientResponse) => String = x => exctractDomain(x)

  private def exctractDomain(x: ClientResponse): String = Try {
    new URL(x.url).getHost.replaceAll("(?i)www?([^.]+).()", "")
  } match {
    case Success(url) => url
    case Failure(ex) => throw ex
  }
}

class RandomWebProcessor(limit: Int, url: String)(implicit system: ActorSystem) extends WebProcessor {
  implicit val timeout = Timeout(5 minutes)

  def createActor = system.actorOf(BulkControllerActor.props(limit, url),
    s"bulkControllerActor-${UUID.randomUUID().toString}")

  lazy val results = {
    val actorRef = createActor
    val result = actorRef ? BulkControllerActor.Initialize
    result.map {
      anyRef =>
        val listOfResults = anyRef.asInstanceOf[List[ClientResponse]]
        actorRef ! Shutdown
        listOfResults
    }

  }
}
