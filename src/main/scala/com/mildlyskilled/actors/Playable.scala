package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models._


trait Playable extends Actor with ActorLogging {

  val knownInsults: List[Insult]
  val knownComebacks: List[Comeback]
  implicit val insults = knownInsults
  implicit val comebacks = knownComebacks


  def receive = {

    case Select(x) =>
      insults find { i => i.id == x } match {
        case Some(insult) => sender ! InsultMessage(insult)
        case None => sender ! Info("You do not know this Insult")
      }

    // now we must find a good comeback to this insult
    case InsultMessage(x) =>
      comebacks find { c => c.id == x.id } match {
        case Some(comeback) => sender ! ComebackMessage(comeback)
        case None => sender ! ConcedeRound
      }

    case ComebackMessage(c) => sender ! ConcedeRound

    case Leave => context stop self
  }
}