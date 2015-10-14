package com.example

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Future
import scala.language.postfixOps

/**
 * Created by janko on 14/10/15.
 */
class BulkCollectorActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("BulkCollectorActorSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A bulk collector actor" must {
    "return as many responses as defined in the limit" in {
      val controllerActor = system.actorOf(Props(TestBulkActor))
      controllerActor ! BulkControllerActor.Initialize
      expectMsgClass(classOf[List[ResponseWithStatus]])

    }
  }

}

object TestBulkActor extends BulkController(TestData.limit, "www.someSite") {
  override val asyncClient: HttpClient = new TestHttpClient
  override def requestActor: ActorRef = context.actorOf(RequestActor.props(asyncClient), s"requestActor-${UUID.randomUUID().toString}")
}


class TestHttpClient extends HttpClient {
  override def get(url: String): Future[ClientResponse] = Future.successful(TestData.resultList(TestData.rand.nextInt(TestData.resultList.size)))
}