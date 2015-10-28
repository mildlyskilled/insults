package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Comeback, Insult, Entry}

import scala.collection.mutable


trait Playable extends Actor with ActorLogging {

  val knownInsults: List[Insult]
  val knownComebacks: List[Comeback]

  def receive = {
    case InsultMessage(insult) => handleInsult(insult)

    case ComebackMessage(comeback) => handleComeback(comeback)

    case GetInsults => handleReturnInsults()

    case GetComebacks => handleReturnComebacks()

    case SelectInsult(id) => handleSelectInsult(id)

    case ConcedeGame => handleConcedeGame()

    case GoAway => handleGoAway()

    case Leave => context.system.shutdown()
  }

  def handleInsult(i: Insult) = {
    log.info(Console.BLUE + s"${i.content}" + Console.RESET)

    val found = for {
      c <- knownComebacks if c.id == i.id
    } yield c

    if (found.isEmpty) {
      sender() ! ConcedeRound
    } else {
      sender() ! ComebackMessage(found.head)
    }
  }

  def handleComeback(c: Comeback) = {
    log.info(Console.GREEN + s"${c.content}" + Console.RESET)
    sender() ! ConcedeRound
  }

  def handleReturnInsults() = {
    sender() ! KnownInsults(knownInsults)
  }

  def handleSelectInsult(id: Int) = {
    val insultList = knownInsults.filter(_.id == id)
    if (insultList.isEmpty){
      log.info(Console.RED + "You do not know this insult yet" + Console.RESET)
    }else{
      sender() ! InsultMessage(insultList.head)
    }
  }

  def handleGoAway() = {
    log.info(Console.RED + s"${self.path.name} leaving now" + Console.RESET)
    context stop self
  }

  def handleConcedeGame() = {
    log.info(Console.RED + s"I have lost ${self.path.name} concedes" + Console.RESET)
    context stop self
  }

  def handleReturnComebacks() = {
    sender() ! KnownComebacks(knownComebacks)
  }

}
