package com.mildlyskilled.messages

import akka.actor.ActorRef
import com.mildlyskilled.models.{Comeback, Insult}

sealed trait Message
case class InsultMessage(target: ActorRef, insult: Insult) extends Message
case class ComebackMessage(target: ActorRef, comeback: Comeback) extends Message
case object Initialise
case object Leave