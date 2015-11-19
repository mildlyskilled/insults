package com.mildlyskilled.actors

import akka.actor._
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.Repo

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  var registry = mutable.Map.empty[ActorRef, Int]
  var toComeback: ActorRef = _
  var toInsult: ActorRef = _

  import context._

  def waiting: Receive = {
    case Register(player) => handleRegister(player)
    case Unregister(player) => handleUnregister(player)
    case ListPlayers => handleListPlayers()
  }

  def ready: Receive = {
    case Initialise =>
      sender ! Info("The game engine has already been initialised")

    case Register(player) =>
      player ! Info("Sorry the room is full")

    case Unregister(player) =>
      registry -= player
      player ! Leave
      become(waiting)

    case ReadyToEngage =>
      log.info(s"${sender.path.name} is ready")
      sender ! YourTurn

    case ListPlayers => handleListPlayers()

    case InsultMessage(insult) =>
      registry.keys.filterNot(_ == sender).head.tell(InsultMessage(insult), self)

    case GetScores =>
      registry.foreach { p => println(s"${p._1.path.name}: ${p._2}") }

    case ConcedeRound =>
      log.info(Console.RED + s"${sender().path.name} concedes this round" + Console.RESET)
      registry.values.filterNot(_ != sender).head + 1

      if (registry(sender()) == 0) {
        sender ! ConcedeGame
        registry -= sender
        log.info(s"${sender().path.name} leaves")
      }

  }

  def receive = {
    case Initialise =>
      log.info(s"Starting game engine ${self.path.name}")
      become(waiting)
  }

  def handleRegister(player: ActorRef) = {
    log.info(s"${player.path.name} has entered the game")
    registry += (player -> 2)
    player ! Registered
  }

  def handleUnregister(player: ActorRef) = {
    log.info(s"${player.path.name} is leaving the game")
    registry -= player
    player ! Leave
  }

  def handleListPlayers() = {
    println("Current players in this game")
    registry.foreach { p => println(p._1.path.name) }
  }
}
