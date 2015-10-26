package com.mildlyskilled

import akka.actor.{Props, ActorSystem}
import com.mildlyskilled.actors.{GameEngine, Player}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models._

import scala.tools.jline.console.ConsoleReader


object Application extends App {

  val repository = new Repo

  val insultRegex = """^i (\d+)$""".r
  val comebackRegex = """^c (\d+)$""".r

  val system = ActorSystem("InsultSystem")
  val gameEngine = system.actorOf(Props(classOf[GameEngine], repository), name = "gameEngine")
  val playerActor = system.actorOf(Props(classOf[Player], repository.getRandomInsults(2)), name = "player")

  Iterator.continually(new ConsoleReader().readLine("> ")).takeWhile(_ != "x").foreach {
    case "s" => {
      gameEngine ! Initialise
      playerActor.tell(GetInsults, gameEngine)
    }
    case "m" => {
      playerActor ! GetInsults
    }
    case insultRegex(id) => {
      playerActor.tell(SelectInsult(id.toInt), gameEngine)
    }
    case _ => println("I did not understand that message")
  }
  println("Exiting game...")
  system.shutdown()
}
