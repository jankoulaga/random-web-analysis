package com.example

import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * Created by janko on 13/10/15.
 */
class WebProcessorSpec extends FlatSpec with Matchers with WebProcessor {
  lazy val timeout = 1 second

  "Web processor must" should "return only the successful,sorted results" in {
    val sortedResults = Await.result(getSortedResults, timeout)
    val resultsToPrint = Await.result(getResultsAsString, timeout)
    sortedResults.size should ===(22)
    resultsToPrint should equal(TestData.expectedResult)
    resultsToPrint.size shouldEqual TestData.expectedResult.size
  }

  override val results: Future[List[ClientResponse]] = Future.successful(TestData.resultList)

}
