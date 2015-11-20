package com.mildlyskilled.actors

import akka.actor._
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Insult, Arena, Repo}

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  var registry = mutable.Seq.empty[Arena]

  import context._

  def waiting: Receive = {
    case Register(player) => handleRegister(player)
    case Unregister(player) => handleUnregister(player)
    case ListPlayers => handleListPlayers()
  }

  def ready: Receive = {
    case Initialise =>
      sender ! Info("The game engine has already been initialised")

    case Register(player) => handleRegister(player)

    case Unregister(player) =>
      player ! Leave
      become(waiting)

    case ReadyToEngage =>
      log.info(s"${sender.path.name} is ready")
      sender ! YourTurn

    case ListPlayers => handleListPlayers()

    case InsultMessage(insult) => handleInsultMessage(insult)

    case GetScores => arena.head.players.foreach { p => println(s"${p._1.path.name}: ${p._2.toString}") }

    case ConcedeRound => handleConcedeRound()

  }

  def handleRegister(player: ActorRef) = {
    log.info(s"${player.path.name} has entered the game")
    registry = registry :+ Arena(player.path.name, mutable.Map(player -> 0))
    println(registry)
    player ! Registered
  }

  def handleUnregister(player: ActorRef) = {
    log.info(s"${player.path.name} is leaving the game")
    registry = registry.filter(a => a.name == player.path.name)
    player ! Leave
  }

  def handleListPlayers() = {
    println("Current players in this game")
    getArena(sender()).players foreach println

  }

  def handleInsultMessage(insult: Insult) = {
    if (getArena(sender()).players.nonEmpty)
      getArena(sender()).getPlayers.filterNot(player => player != sender()).foreach(_ ! InsultMessage(insult))
  }

  def handleConcedeRound() = {
    log.info(Console.RED + s"${sender().path.name} concedes this round" + Console.RESET)
    getArena(sender()).players.filterNot(player => player._1 == sender()).foreach {
      opponent => {
        getArena(sender()).addScore(opponent._1)
        opponent._1 ! YourTurn
      }
    }
  }

  def getArena(player: ActorRef) = {
    registry.filter(arena => arena.name == sender.path.name).head
  }


  def receive = {
    case Initialise =>
      log.info(s"Starting game engine ${self.path.name}")
      become(waiting)
  }

}
