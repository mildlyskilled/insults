package com.mildlyskilled.actors

import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Insult, Comeback}
import scala.collection.mutable

case class Player(override val knownInsults: List[Insult], override val knownComebacks: List[Comeback])
  extends Playable {

  var learnedComebacks = mutable.Set.empty[Comeback]
  var learnedInsults = mutable.Set.empty[Insult]
  override implicit val insults = knownInsults ::: learnedInsults.toList

  override def handleComeback(c: Comeback) = {
    log.info(Console.GREEN + s"${c.content}" + Console.RESET)
    learnedComebacks += c
    sender() ! ConcedeRound
  }

  override def handleReturnInsults()(implicit insults: List[Insult]): Unit = {
    sender() ! KnownInsults(insults)
  }

  override def handleReturnComebacks()(implicit comebacks: List[Comeback]) = {
    sender() ! KnownComebacks(knownComebacks ::: learnedComebacks.toList)
  }

}
