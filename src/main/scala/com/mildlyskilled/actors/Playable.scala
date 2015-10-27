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

    case SelectInsult(id) => handleSelectInsult(id)

    case ConcedeGame => handleConcedeGame()

    case GoAway => handleGoAway()

    case Leave => context.system.shutdown()
  }

  def handleInsult(i: Insult) = {
    log.info(s"${self.path.name} received insult ${i.id} from ${sender.path.name}")
    knownInsults foreach println
    if (knownComebacks.takeWhile(_.id == i.id).isEmpty) {
      sender() ! ConcedeRound
    } else {
      sender() ! ComebackMessage(knownComebacks.takeWhile(_.id == i.id).head)
    }
  }

  def handleComeback(c: Comeback) = {
    log.info(s"${self.path.name} received comeback ${c.id}")
    sender() ! ConcedeRound
  }

  def handleReturnInsults() = {
    sender() ! KnownInsults(knownInsults)
  }

  def handleSelectInsult(id: Int) = {
    sender() ! InsultMessage(knownInsults.filter(_.id == id).head)
  }

  def handleGoAway() = {
    log.info("Received go way message")
    context stop self
  }

  def handleConcedeGame() = {
    log.info(s"I have lost ${self.path.name} concedes")
    context stop self
  }

}
