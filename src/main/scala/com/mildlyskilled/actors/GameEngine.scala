package com.mildlyskilled.actors

import akka.actor._
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Repo}

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  var registry = mutable.Map.empty[ActorRef, Int]
  var toComeback:ActorRef = _
  var toInsult:ActorRef = _


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

    case InsultMessage(insult) => {
      if (registry.size < 2) {
        log.info("We don't have all players required to play did you remember to type 's' to start?")
      } else {
        if (toInsult == sender()){
          registry.keys.filterNot(_ == sender).head.tell(InsultMessage(insult), self)
        }else{
          sender() ! Info("It's not your turn to insult")
        }
      }

    }

    case ComebackMessage(entry) => {
      if (registry.size < 2) {
        log.info("We don't have all players required to play did you remember to type 's' to start?")
      } else {
        if (toComeback == sender()) {
          registry.keys.filterNot(_ == sender).head.tell(ComebackMessage(entry), self)
        } else {
          sender() ! Info("It's not your turn to comeback")
        }
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
      log.info(Console.RED + s"${sender().path.name} concedes this round" + Console.RESET)
      registry(sender()) = registry(sender()) - 1

      if (registry(sender()) == 0) {
        sender ! ConcedeGame
        registry -= sender
        log.info(s"${sender().path.name} leaves")
      }

    }

    case KnownInsults(insults) => {
      println(s"---------- Known Insults from ${sender().path.name}")
      insults.foreach { i =>
        println(s"[${i.id}]: ${i.content}")
      }
    }

    case KnownComebacks(comebacks) => {
      println(s"---------- Known Comebacks from ${sender().path.name}")
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

    case WaitingForEngagement => toComeback = sender()

    case ReadyToEngage => toInsult = sender()

    case Turn => {
      log.info(Console.GREEN + toInsult.path.name + " to insult " + Console.RESET)
      log.info(Console.GREEN + toComeback.path.name + " to comeback " + Console.RESET)
    }
  }
}
