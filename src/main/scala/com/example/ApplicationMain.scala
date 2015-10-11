package com.example

import akka.actor.ActorSystem

object ApplicationMain extends App {
  implicit lazy val System = ActorSystem("RandomWebAnalyzerSystem")
  val processor = new RandomWebProcessor(100, "http://www.randomwebsite.com/cgi-bin/random.pl")
  val results = processor.printResults
  System.awaitTermination()
}