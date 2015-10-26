package com.mildlyskilled.messages

import akka.actor.ActorRef
import com.mildlyskilled.models.Entry

sealed trait Message
case class InsultMessage(insult: Entry) extends Message
case class ComebackMessage(comeback: Entry) extends Message
case class KnownInsults(insults: List[Entry]) extends Message
case class SelectInsult(id: Int) extends Message
case object GetInsults extends Message
case object Initialise
case object Leave
