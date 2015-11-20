import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.tools.jline.console.ConsoleReader

import com.mildlyskilled.actors.Player
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.Repo
import com.typesafe.config.ConfigFactory

object Client extends App {

  implicit val resolveTimeout = Timeout(5 seconds)
  val repo = new Repo
  val system = ActorSystem("InsultSystem", ConfigFactory.load.getConfig("player"))
  val serverConfig = ConfigFactory.load.getConfig("engine")
  val serverHostName = serverConfig.getString("akka.remote.netty.tcp.hostname")
  val serverPort = serverConfig.getString("akka.remote.netty.tcp.port")
  val serverPath = s"akka.tcp://InsultSystem@$serverHostName:$serverPort/user/engine"
  val gameEngine = Await.result(system.actorSelection(serverPath).resolveOne, resolveTimeout.duration)

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

    case "start" =>
      if (!started) {
        gameEngine ! Register(playerActor)
        started = true
      } else {
        println(Console.RED + "You are already in a game" + Console.RESET)
      }


    case "play" => gameEngine ! ReadyToEngage

    case "list" => gameEngine ! ListPlayers

    case numberSelectorPattern(x) => playerActor.tell(Select(x.toInt), gameEngine)


    case _ => println("I did not understand that message")
  }

  gameEngine ! Unregister(playerActor)
  playerActor ! Leave
  system.shutdown()
}
