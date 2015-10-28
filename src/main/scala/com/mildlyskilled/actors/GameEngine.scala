package com.mildlyskilled.actors

import akka.actor._
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Repo}

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  var registry = mutable.Map.empty[ActorRef, Int]


  def receive = {
    case Initialise => log.info(s"Starting game engine ${self.path.name}")

    case Register(player) => {
      log.info(s"${player.path.name} has entered the game")
      registry += (player -> 2)
    }

    case Unregister(player) => {
      registry -= player
      player ! GoAway
    }

    case InsultMessage(entry) => {
      if (registry.size < 2) {
        log.info("We don't have all players required to play did you remember to type 's' to start?")
      } else {
        registry.keys.filterNot(_ == sender).head.tell(InsultMessage(entry), self)
      }

    }

    case ComebackMessage(entry) => {
      if (registry.size < 2) {
        log.info("We don't have all players required to play did you remember to type 's' to start?")
      } else {
        registry.keys.filterNot(_ == sender).head.tell(ComebackMessage(entry), self)
      }
    }

    case SelectInsult(id) => {
      sender() ! SelectInsult(id)
    }

    case ListPlayers => {
      println("Current players in this game")
      registry.foreach { p => println(p._1) }
    }

    case ConcedeRound => {
      log.info(Console.RED + s"${sender.path.name} concedes this round" + Console.RESET)
      registry(sender) = registry(sender) - 1

      if (registry(sender) == 0) {
        sender ! ConcedeGame
        registry -= sender
        log.info(s"${sender.path.name} leaves")
      }

    }

    case KnownInsults(insults) => {
      println(s"---------- Known Insults from ${sender.path.name}")
      insults.foreach { i =>
        println(s"[${i.id}]: ${i.content}")
      }
    }

    case KnownComebacks(comebacks) => {
      println(s"---------- Known Comebacks from ${sender.path.name}")
      comebacks.foreach { i =>
        println(s"[${i.id}]: ${i.content}")
      }
    }

    case GetScores => {
      registry.foreach { p => println(s"${p._1.path.name}: ${p._2}") }
    }

    case ResetPlayerScore(p) => {
      registry(p) = 2
    }
  }
}
