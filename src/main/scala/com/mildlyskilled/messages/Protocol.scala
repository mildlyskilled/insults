package com.mildlyskilled.messages

import akka.actor.ActorRef
import com.mildlyskilled.models.{Comeback, Insult, Entry}

object Protocol {

  sealed trait Message

  final case class Info(msg: String) extends Message

  final case class InsultMessage(insult: Insult) extends Message

  final case class ComebackMessage(comeback: Comeback) extends Message

  final case class KnownInsults(insults: List[Insult]) extends Message

  final case class KnownComebacks(insults: List[Comeback]) extends Message

  final case class Select(id: Int) extends Message

  final case class SelectInsult(id: Int) extends Message

  final case class Register(player: ActorRef) extends Message

  final case class Unregister(player: ActorRef) extends Message

  final case class ResetPlayerScore(player: ActorRef) extends Message

  case object YourTurn extends Message

  case object Registered extends Message

  case object GoAway extends Message

  case object WaitingForEngagement extends Message

  case object ReadyToEngage extends Message

  case object GetInsults extends Message

  case object GetComebacks extends Message

  case object ConcedeRound extends Message

  case object ConcedeGame extends Message

  case object Initialise extends Message

  case object AnotherGame extends Message

  case object ListPlayers extends Message

  case object GetScores extends Message

  case object Leave extends Message
}
