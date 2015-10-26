package com.mildlyskilled.messages

import akka.actor.ActorRef
import com.mildlyskilled.models.Entry

object Protocol {
  sealed trait Message
  case class ForwardInsult(insult: Entry, sender: ActorRef) extends Message
  case class InsultMessage(insult: Entry) extends Message
  case class ComebackMessage(comeback: Entry) extends Message
  case class KnownInsults(insults: List[Entry]) extends Message
  case class SelectInsult(id: Int) extends Message
  case object GetInsults extends Message
  case object ConcedeRound extends Message
  case object ConcedeGame extends Message
  case object Initialise extends Message
  case object AnotherGame extends Message
  case object Leave extends Message
}
