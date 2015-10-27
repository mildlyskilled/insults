package com.mildlyskilled

import akka.actor.{ActorRef, Props, ActorSystem}
import com.mildlyskilled.actors.{Pirate, GameEngine, Player}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models._

import scala.tools.jline.console.ConsoleReader


object Application extends App {

  val repo = new Repo

  val insultRegex = """^i (\d+)$""".r
  val comebackRegex = """^c (\d+)$""".r

  val system = ActorSystem("InsultSystem")
  val gameEngine = system.actorOf(Props(classOf[GameEngine], repo), name = "insult-sword-fighting")
  val same = repo.getRandomInsults(2)
  def spawnPirate: ActorRef = system.actorOf(Props(classOf[Pirate], same, repo.getRandomComebacks(2)),
    name="pirate")

  val playerActor = system.actorOf(Props(classOf[Player], same, repo.getRandomComebacks(2)),
    name = "player")

  var pirateActor: ActorRef = spawnPirate

  var started = false

  def printHelp():Unit = {
    println("Welcome Instructions are as follows (type)")
    println("-----------------------------------------")
    println("start - Start game")
    println("play - Get your list of insults to play with")
    println("players - Get list of players")
    println("stop - dispenses with the current pirate pirate")
    println("another - Get another pirate to duel with")
    println("pirate-insults - Get a list of insults known by the pirate")
    println("Scores - Get a list of insults known by the pirate")
    println("i <insult-id> - Insult your opponent")
    println("c <comeback-id> - Reply with a witty comeback")
    println("help - Print this help")
    println("exit - Exit this game")
  }


  Iterator.continually(new ConsoleReader().readLine("> ")).takeWhile(_ != "exit").foreach {
    case "start" => {
      if (!started) {
        gameEngine ! Initialise
        gameEngine ! Register(playerActor)
        gameEngine ! Register(pirateActor)
        started = true
      }else{
        println("Game already started please use restart instead")
      }
    }

    case "another" => {
      pirateActor = spawnPirate
      gameEngine ! Register(pirateActor)
    }

    case "play" => playerActor.tell(GetInsults, gameEngine)

    case "players" => gameEngine ! ListPlayers

    case "stop" => {
      gameEngine ! Unregister(pirateActor)
    }

    case "pirate-insults" => {
      pirateActor.tell(GetInsults, gameEngine)
    }

    case "scores" => gameEngine ! GetScores

    case insultRegex(id) => {
      playerActor.tell(SelectInsult(id.toInt), gameEngine)
    }

    case "help" => printHelp()

    case _ => println("I did not understand that message")
  }
  println("Exiting game...")
  system.shutdown()
}
