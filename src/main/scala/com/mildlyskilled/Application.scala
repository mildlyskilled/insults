package com.mildlyskilled

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import com.mildlyskilled.actors.{Player, Pirate}
import com.mildlyskilled.messages._
import com.mildlyskilled.models._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.tools.jline.console.ConsoleReader


object Application extends App {

  val repository = new Repo

  def randomInsults = scala.util.Random.shuffle(repository.entries).take(2)

  val insultRegex = """^i (\d+)$""".r
  val comebackRegex = """^c (\d+)$""".r

  val system = ActorSystem("InsultSystem")
  val playerActor = system.actorOf(Props(classOf[Player], randomInsults), name = "player")
  val pirateActor = system.actorOf(Props(classOf[Pirate],randomInsults), name = "pirate")

  Iterator.continually(new ConsoleReader().readLine("> ")).takeWhile(_ != "x").foreach {
    case "s" => {
      playerActor ! GetInsults
    }
    case insultRegex(id) => {
      val insult = repository.entries.filter(_.id == id).head
      playerActor ! SelectInsult(id.toInt)
      pirateActor ! InsultMessage(insult)
    }
  }
  println("Exiting game...")
  system.shutdown()
}
