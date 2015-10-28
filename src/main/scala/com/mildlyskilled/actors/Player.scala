package com.mildlyskilled.actors

import akka.actor.{Actor, ActorLogging}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.messages._
import com.mildlyskilled.models.{Insult, Comeback, Entry}
import scala.collection.mutable
import scala.concurrent.Future

case class Player(override val knownInsults: List[Insult], override val knownComebacks: List[Comeback])
  extends Playable {

  var learnedComebacks = mutable.Set.empty[Comeback]
  var learnedInsults = mutable.Set.empty[Insult]

  override def handleInsult(i: Insult) = {
    log.info(Console.BLUE + s"${i.content}" + Console.RESET)
    if ((knownInsults:::learnedInsults.toList).takeWhile(_.id == i.id).isEmpty) {
      learnedInsults += i
      sender() ! ConcedeRound
    } else {
      sender() ! ComebackMessage((knownComebacks:::learnedComebacks.toList).takeWhile(_.id == i.id).head)
    }
  }

  override def handleComeback(c: Comeback) = {
    log.info(Console.GREEN + s"${c.content}" + Console.RESET)
    learnedComebacks += c
    sender() ! ConcedeRound
  }

  override def handleReturnInsults() = {
    sender() ! KnownInsults(knownInsults ::: learnedInsults.toList)
  }

  override def handleReturnComebacks() = {
    sender() ! KnownComebacks(knownComebacks:::learnedComebacks.toList)
  }

  override def handleSelectInsult(id: Int) = {
    sender() ! InsultMessage((knownInsults:::learnedInsults.toList).filter(_.id == id).head)
  }
}
