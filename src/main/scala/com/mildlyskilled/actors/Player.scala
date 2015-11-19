package com.mildlyskilled.actors

import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.{Insult, Comeback}
import scala.collection.mutable

case class Player(override val knownInsults: List[Insult], override val knownComebacks: List[Comeback])
  extends Playable {

  import context._

  var learnedComebacks = mutable.Set.empty[Comeback]
  var learnedInsults = mutable.Set.empty[Insult]
  override implicit val insults = knownInsults ::: learnedInsults.toList
  override implicit val comebacks = knownComebacks ::: learnedComebacks.toList

  def awaitingStatus: Receive = {
    case YourTurn =>
      insults foreach{ i  => println(s"[${i.id}] ${i.content}") }
      become(insulter)

    case InsultMessage(i) =>
      become(insulted)
      self ! InsultMessage(i)

    case ComebackMessage(c) =>
      become(insulter)
      self ! ComebackMessage(c)

  }

  def insulted: Receive = {
    case Select(x) =>
      comebacks find { c => c.id == x } match {
        case Some(comeback) => sender ! ComebackMessage(comeback)
        case None => sender ! Info("You do not know this comeback")
      }

    case InsultMessage(i) =>
      comebacks find { c => c.id == i.id } match {
        case Some(comeback) => sender ! ComebackMessage(comeback)
        case None =>
          sender ! ConcedeRound
          learnedInsults += i
          become(awaitingStatus)
      }

  }

  def insulter: Receive = {
    case Select(x) =>
      insults find {i => i.id == x } match {
        case Some(insult) =>
          sender ! InsultMessage(insult)
          become(awaitingStatus)

        case None => sender ! Info("You do not know this insult")
      }

    case InsultMessage(x) => sender ! Info("It's my turn to insult not yours")

    case ComebackMessage(x) =>
      sender ! ConcedeRound
      become(awaitingStatus)
  }

  override def receive = {
    case Registered =>
      log.info(s"Registered to play send ready to engage message to ${sender.path.name}")
      sender ! ReadyToEngage
      become(awaitingStatus)

    case YourTurn =>
      log.info("I still don't have a state but that will change pretty soon")
      become(insulter)
  }
}
