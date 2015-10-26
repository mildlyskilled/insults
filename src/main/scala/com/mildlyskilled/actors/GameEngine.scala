package com.mildlyskilled.actors

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Repo}

import scala.collection.mutable

class GameEngine(val repo: Repo) extends Actor with ActorLogging {

  def spawnPirate: ActorRef = context.actorOf(Props(classOf[Pirate], repo.getRandomInsults(2)), name="pirate")
  var pirateActor: ActorRef = spawnPirate
  val scores:mutable.Map[ActorRef, Int] = mutable.Map(pirateActor -> 2)

  def receive = {
    case Initialise => {
      log.info("Starting game engine")
      log.info(s"Pirate Ready: ${pirateActor.path.name}")
    }
    case AnotherGame => {
      pirateActor = spawnPirate
    }
    case InsultMessage(entry) => {
      log.info("Forwarding insult")
       pirateActor.tell(InsultMessage(entry), self)
    }
    case ConcedeRound => {
      log.info(s"${sender.path.name} concedes this round")

      if (scores contains sender) {
        if (scores(sender) == 0)
          sender ! ConcedeGame
        scores(sender) = scores(sender) - 1
      }else{
        scores + (sender -> 1)
      }

      println(scores)
    }
    case KnownInsults(insults) => {
      println(s"---------- Known Insults from ${sender.path.name}")
      insults.foreach { i =>
        println(s"[${i.id}]: ${i.generalInsult}")
      }
    }
  }
}