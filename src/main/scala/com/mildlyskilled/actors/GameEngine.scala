package com.mildlyskilled.actors

import akka.actor._
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Arena, Comeback, Insult, Repo}

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  var registry = mutable.Seq.empty[Arena]

  import context._

  def waiting: Receive = {
    case Register => handleRegister()
    case Unregister => handleUnregister()
    case ListPlayers => handleListPlayers()
    case ReadyToEngage => handleReadyToEngage()
  }

  def ready: Receive = {
    case Initialise =>
      sender ! Info("The game engine has already been initialised")

    case Register => handleRegister()

    case Unregister => handleUnregister()

    case ListPlayers => handleListPlayers()

    case InsultMessage(insult) => handleInsultMessage(insult)

    case ComebackMessage(comeback) => handleComebackMessage(comeback)

    case PrintScores => getArenaByPlayer(sender()) match {
      case Some(arena) => arena.players.foreach { p => println(s"${p._1.path.name}: ${p._2.toString}") }
      case None => log.info(Console.RED + " This player doesn't belong to an arena " + Console.RESET)
    }

    case ConcedeRound => handleConcedeRound()

    case ReadyToEngage => handleReadyToEngage()

  }

  def handleComebackMessage(comeback: Comeback): Unit = {
    log.info(s"Got comeback from ${sender.path.name}")
    getArenaByPlayer(sender()) match {
      case Some(arena) => arena.getPlayers.filterNot(_ == sender()).foreach{
        opponent => opponent ! ComebackMessage(comeback)
          arena.incrementScore(sender())
      }
      case None => sender() ! Info("You don't seem to be registered in an arena")
    }
  }

  def handleRegister() = {
    log.info(s"${sender.path.name} has entered the game")
    val arena = Arena(sender.path.name, mutable.Map.empty[ActorRef, Int])
    arena.addPlayer(sender())
    registry = registry :+ arena
    sender() ! Registered
  }

  def handleUnregister() = {
    log.info(s"${sender.path.name} is leaving the game")
    registry.find(arena => arena.name == sender.path.name) match {
      case Some(a) => a.getPlayers.foreach(_ ! Leave)
      case None => sender() ! Leave
    }
    registry = registry.filterNot(a => a.name == sender.path.name)

  }

  def handleListPlayers() = {
    println(Console.GREEN + "Current players in this game" + Console.RESET)
    getArenaByPlayer(sender()) match {
      case Some(arena) => arena.getPlayers.foreach { p => println("> " + Console.GREEN + p.path.name + Console.RESET) }
      case None => log.info("You don't seem to belong to any arenas")
    }
  }

  def handleInsultMessage(insult: Insult) = {
      getArenaByPlayer(sender()) match  {
        case Some(arena) => arena.getPlayers.filterNot(p => p == sender()).foreach(_ ! InsultMessage(insult))
        case None => Info("You are not in an arena")
      }
  }

  def handleConcedeRound() = {
    log.info(Console.RED + s"${sender().path.name} concedes this round" + Console.RESET)
    getArenaByPlayer(sender()) match {
      case Some(arena) => arena.getPlayers.filterNot(player => player == sender()).foreach {
        opponent => {
          arena.incrementScore(opponent)
          opponent ! Info("You win this round")
          opponent ! YourTurn
          if (arena.getPlayerScore(opponent) == arena.scoreLimit) {
            // if the arena belongs to the sender reset the sender score and remove pirate for another duel
            if (opponent.path.name == arena.name) {
              opponent ! Info("You are victorious")
              sender() ! Leave
              arena.resetScore(opponent)
              arena.removePlayer(sender())
            }
          }

        }
      }
      case None => log.info(s"${sender.path.name} doesn't appear to have an arena")
    }

  }

  def handleReadyToEngage() = {
    log.info(s"${sender.path.name} is ready")
    getArenaByPlayer(sender) match {

      case Some(arena) => {
        getArenaByName("pirate") match {
          case Some(pirateArena) => sender() ! Info("You're already engaged in battle")
          case None => arena.addPlayer(spawnPirate("pirate"))
        }
      }

      case None => sender() ! Info("You don't seem to be in an arena")
    }
    sender() ! YourTurn
    become(ready)
  }

  def getArenaByName(arenaName: String) = {
    registry.find(arena => arena.name == arenaName)
  }

  def getArenaByPlayer(player: ActorRef) = registry.find(arena => arena.players.contains(player))

  def spawnPirate(name: String) = {
    context.system.actorOf(
      Props(classOf[Pirate], repo.getRandomInsults(10), repo.getRandomComebacks(10)),
      name = name)
  }


  def receive = {
    case Initialise =>
      log.info(s"Starting game engine: ${self.path.name}")
      become(waiting)

  }

}
