package com.example

import scala.util.Random

/**
 * Created by janko on 14/10/15.
 */
object TestData {
  val limit = 10
  val exception = new RuntimeException
  val rand = Random
  val resultList : List[ClientResponse] = List(
    ResponseWithError(exception, "http://a"),
    ResponseWithError(exception, "http://aaa"),
    ResponseWithError(exception, "http://b"),
    ResponseWithStatus(200, "http://a"),
    ResponseWithStatus(200, "http://ab"),
    ResponseWithStatus(200, "http://ac"),
    ResponseWithStatus(200, "http://b"),
    ResponseWithStatus(200, "http://c"),
    ResponseWithStatus(200, "http://d"),
    ResponseWithStatus(200, "http://ab.com"),
    ResponseWithStatus(200, "http://www.abc.com"),
    ResponseWithStatus(305, "https://abd.com"),
    ResponseWithError(exception, "http://b"),
    ResponseWithError(exception, "http://bd"),
    ResponseWithError(exception, "http://d"),
    ResponseWithError(exception, "http://r"),
    ResponseWithError(exception, "http://x"),
    ResponseWithStatus(200, "http://www.wiwiwiw.com"),
    ResponseWithStatus(200, "http://wwww.sadlje"),
    ResponseWithStatus(301, "http://a"),
    ResponseWithStatus(304, "http://a"),
    ResponseWithStatus(200, "http://a")
  )

  val expectedResult = List(
    "http://a",
    "http://a, 301",
    "http://a, 304",
    "http://a",
    "http://ab",
    "http://ab.com",
    "http://www.abc.com",
    "https://abd.com, 305",
    "http://ac",
    "http://b",
    "http://c",
    "http://d",
    "http://wwww.sadlje",
    "http://www.wiwiwiw.com"
  )
}
