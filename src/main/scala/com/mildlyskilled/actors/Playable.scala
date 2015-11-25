package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models._


trait Playable extends Actor with ActorLogging {

  val knownInsults: List[Insult]
  val knownComebacks: List[Comeback]
  implicit def insults = knownInsults
  implicit def comebacks = knownComebacks

  def receive = {
    case Select(x) =>
      insults find { i => i.id == x } match {
        case Some(insult) => sender ! InsultMessage(insult)
        case None => sender ! Info("You do not know this Insult")
      }

    // now we must find a good comeback to this insult
    case InsultMessage(x) =>
      log.info(s"Received insult from ${sender.path.name}")
      comebacks find { c => c.id == x.id } match {
        case Some(comeback) => sender.tell(ComebackMessage(comeback), self)
        case None =>
          print(Console.YELLOW)
          println(s"${self.path.name} Comebacks: ")
          comebacks foreach (x => println(s"${x.id}: ${x.content}"))
          println(Console.RESET)
          sender.tell(ConcedeRound, self)
      }

    case ComebackMessage(c) => sender ! ConcedeRound

    case Registered => log.info("Registered to play")

    case Leave => {
      sender ! Info("My work here is done")
      sender ! ConcedeGame
      context stop self
    }

    case Info(m) => println(Console.YELLOW + m + Console.RESET)

    case YourTurn => log.info(Console.YELLOW + "I will not stoop so low" + Console.RESET)

    case _ => log.info("I did not understand that message")
  }

  override def preStart = {
    log.info(s"Starting ${self.path.name}")
  }
}