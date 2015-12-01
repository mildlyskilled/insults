import akka.actor.{Props, ActorSystem}
import com.mildlyskilled.actors.GameEngine
import com.mildlyskilled.messages.Protocol.Initialise
import com.mildlyskilled.models.Repo
import com.mildlyskilled.network.Selector
import com.typesafe.config.ConfigFactory

/**
  * Created by kwabena on 12/11/2015.
  */
object Engine extends App {

  val repo = new Repo

  val system = ActorSystem("InsultSystem", ConfigFactory.load.getConfig("engine"))
  val gameEngine = system.actorOf(Props(classOf[GameEngine], repo), name = "engine")

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


  gameEngine ! Initialise
}
