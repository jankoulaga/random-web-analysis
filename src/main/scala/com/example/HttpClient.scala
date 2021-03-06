package com.example

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal


trait HttpClient {
  def get(url: String): Future[ClientResponse]
}

sealed trait ClientResponse {
  val url: String
}
case class ResponseWithStatus(statusCode: Int, url: String) extends ClientResponse
case class ResponseWithError(throwable: Throwable, url: String) extends ClientResponse


trait HttpClientProvider {
  val asyncClient: HttpClient
}

trait HttpClientComponent extends HttpClientProvider {
  val asyncClient = AsyncClient
}

object AsyncClient extends HttpClient {
  lazy val Timeout = 2000
  val http: Http = new Http().configure(_.setConnectionTimeoutInMs(Timeout).setRequestTimeoutInMs(Timeout))

  def get(urlString: String): Future[ClientResponse] = {
    val response = for {
      redirectUrl <- redir(urlString)
      result <- http(url(redirectUrl).HEAD).either
    } yield handleResponse(result, redirectUrl)
    response.recover {
      case NonFatal(x) => ResponseWithError(x, "")
    }
  }

  def handleResponse(response: Either[Throwable, Res], url: String): ClientResponse = {
    response match {
      case Right(result) => ResponseWithStatus(result.getStatusCode, url)
      case Left(StatusCode(code)) => ResponseWithStatus(code, url)
      case Left(x) => ResponseWithError(x, url)
    }
  }

  def redir(s: String) = http(url(s).HEAD > (x => x.getHeader("Location")))

}
