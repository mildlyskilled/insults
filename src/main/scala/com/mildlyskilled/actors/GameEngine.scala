package com.mildlyskilled.actors

import akka.actor._
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Repo}

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  var registry = mutable.Map.empty[ActorRef, Int]
  var toComeback: ActorRef = _
  var toInsult: ActorRef = _


  def receive = {
    case Initialise => log.info(s"Starting game engine ${self.path.name}")

    case Register(player) => {
      log.info(s"${player.path.name} has entered the game")
      registry += (player -> 2)
      player ! Registered
    }

    case Unregister(player) => {
      registry -= player
      player ! GoAway
    }

    case ListPlayers => {
      println("Current players in this game")
      registry.foreach { p => println(p._1) }
    }

    case ConcedeRound => {
      log.info(Console.RED + s"${sender().path.name} concedes this round" + Console.RESET)
      registry(sender()) = registry(sender()) - 1

      if (registry(sender()) == 0) {
        sender ! ConcedeGame
        registry -= sender
        log.info(s"${sender().path.name} leaves")
      }

    }

    case GetScores => {
      registry.foreach { p => println(s"${p._1.path.name}: ${p._2}") }
    }

    case ResetPlayerScore(p) => {
      registry(p) = 2
    }

    case Select(x) =>
      registry.foreach(p => p._1.tell(Select(x), sender()))
  }
}
