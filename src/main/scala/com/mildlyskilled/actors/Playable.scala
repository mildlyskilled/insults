package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Comeback, Insult}


trait Playable extends Actor with ActorLogging {

  val knownInsults: List[Insult]
  val knownComebacks: List[Comeback]
  implicit val insults = knownInsults
  implicit val comebacks = knownComebacks

  def receive = {
    case Info(msg) => log.info(Console.YELLOW + msg + Console.RESET)

    case InsultMessage(insult) => handleInsult(insult)

    case ComebackMessage(comeback) => handleComeback(comeback)

    case GetInsults => handleReturnInsults()

    case GetComebacks => handleReturnComebacks()

    case SelectInsult(id) => handleSelectInsult(id)

    case ConcedeGame => handleConcedeGame()

    case GoAway => handleGoAway()

    case Leave => context.system.shutdown()

    case Registered => handleRegisteredMessage()
  }

  def handleInsult(i: Insult)(implicit comebacks: List[Comeback]) = {
    log.info(Console.GREEN + s"${i.content}" + Console.RESET)
    comebacks find (_.id == i.id) match {
      case None => sender() ! ConcedeRound
      case Some(c) => sender() ! ComebackMessage(c)
    }
  }

  def handleReturnInsults()(implicit insults: List[Insult]) = {
    sender() ! insults
  }

  def handleSelectInsult(id: Int)(implicit insults: List[Insult]) = {
    insults find { i => i.id == id } match {
      case None => log.info(Console.RED + "You don't know this insult" + Console.RESET)
      case Some(i) => sender() ! InsultMessage(i)
    }
  }

  def handleReturnComebacks()(implicit comebacks: List[Comeback]) = {
    sender() ! KnownComebacks(comebacks)
  }

  def handleGoAway() = {
    log.info(Console.RED + s"${self.path.name} leaving now" + Console.RESET)
    context stop self
  }

  def handleConcedeGame() = {
    log.info(Console.RED + s"I have lost ${self.path.name} concedes" + Console.RESET)
    context stop self
  }

  def handleComeback(c: Comeback) = {
    log.info(Console.GREEN + s"${c.content}" + Console.RESET)
    sender() ! ConcedeRound
  }

  def handleRegisteredMessage() = {
    sender() ! ReadyToEngage
  }

}