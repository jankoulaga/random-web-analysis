package com.example

import java.util.UUID

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.example.BulkControllerActor.Shutdown

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by janko on 10/10/15.
 */
class RandomWebProcessor(limit: Int, url: String)(implicit system: ActorSystem) {
  implicit val timeout = Timeout(5 minutes)
  lazy val DomainPattern = "(?:https?:\\/\\/)?(?:www\\.)?([A-Za-z0-9._%+-]+)/?.*".r

  def createActor = system.actorOf(BulkControllerActor.props(limit, url),
    s"bulkControllerActor-${UUID.randomUUID().toString}")

  lazy val gatherResults = {
    val actorRef = createActor
    val result = actorRef ? BulkControllerActor.Initialize
    result.map {
      anyRef =>
        val listOfResults = anyRef.asInstanceOf[List[ClientResponse]]
        actorRef ! Shutdown
        listOfResults
    }

  }

  def printResults = gatherResults.map {
    results =>
      sortedResults(results).foreach {
        case ResponseWithStatus(code, siteUrl) =>
          val codeVal = if (code != 200) Some(code.toString) else None
          println(List(Some(siteUrl), codeVal).flatten.mkString(", "))
        case _ => ()
      }

  }

  def sortedResults(results: List[ClientResponse]): List[ClientResponse] = results.sortBy(sortByDomain)


  def sortByDomain: (ClientResponse) => String = x => domainPatternMatch(x)


  def domainPatternMatch(x: ClientResponse): String = x.url match {
    case DomainPattern(domain) => domain
    case _ => x.url
  }
}
