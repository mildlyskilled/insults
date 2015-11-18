import akka.actor.{ActorSystem, Props}
import com.mildlyskilled.actors.Player
import com.mildlyskilled.messages.Protocol.{Unregister, Leave, Register, Select}
import com.mildlyskilled.models.Repo
import com.typesafe.config.ConfigFactory

import scala.tools.jline.console.ConsoleReader

/**
  * Created by kwabena on 13/11/2015.
  */
object Client extends App {

  val repo = new Repo
  val system = ActorSystem("InsultSystem", ConfigFactory.load.getConfig("player"))
  val serverConfig = ConfigFactory.load.getConfig("engine")
  val serverHostName = serverConfig.getString("akka.remote.netty.tcp.hostname")
  val serverPort = serverConfig.getString("akka.remote.netty.tcp.port")
  val serverPath = s"akka.tcp://InsultSystem@$serverHostName:$serverPort/user/engine"
  val gameEngine = system.actorSelection(serverPath)
  println(s"Got game engine at ${gameEngine.pathString}")

  val playerActor = system.actorOf(
    Props(classOf[Player], repo.getRandomInsults(2), repo.getRandomComebacks(2)),
    name = "player")


  var started = false
  val numberSelectorPattern = """(\d+)$""".r


  def printHelp(): Unit = {
    println("Welcome Instructions are as follows (type)")
    println("-----------------------------------------")
    println("start - Start game")
    println("play - Get your list of insults to play with")
    println("players - Get list of players")
    println("stop - dispenses with the current pirate pirate")
    println("another - Get another pirate to duel with")
    println("pirate - Get a list of insults known by the pirate")
    println("Scores - Get a list of insults known by the pirate")
    println("i <insult-id> - Insult your opponent")
    println("c <comeback-id> - Reply with a witty comeback")
    println("help - Print this help")
    println("exit - Exit this game")
  }


  Iterator.continually(new ConsoleReader().readLine("> ")).takeWhile(_ != "exit").foreach {

    case "start" => {
      if (!started) {
        gameEngine.tell(Register(playerActor), playerActor)
        started = true
      } else {
        println(Console.RED + "You are already in a game" + Console.RESET)
      }
    }

    case numberSelectorPattern(x) => {
      gameEngine.tell(Select(x.toInt), playerActor)
    }

    case _ => println("I did not understand that message")
  }

  gameEngine ! Unregister(playerActor)
  playerActor ! Leave
  system.shutdown()
}
