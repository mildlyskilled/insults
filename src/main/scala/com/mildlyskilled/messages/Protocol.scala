package com.mildlyskilled.messages

import akka.actor.ActorRef
import com.mildlyskilled.models.{Comeback, Insult, Entry}

object Protocol {

  sealed trait Message

  case class Info(msg: String) extends Message

  case class InsultMessage(insult: Insult) extends Message

  case class ComebackMessage(comeback: Comeback) extends Message

  case class KnownInsults(insults: List[Insult]) extends Message

  case class KnownComebacks(insults: List[Comeback]) extends Message

  case class Select(id: Int) extends Message

  case class SelectInsult(id: Int) extends Message

  case class Register(player: ActorRef) extends Message

  case class Unregister(player: ActorRef) extends Message

  case class ResetPlayerScore(player: ActorRef) extends Message

  case object Turn extends Message

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

  sealed trait State

  case object Insulting extends State

  case object Insulted extends State

  sealed trait Data

  case object Uninitialised extends Data

  final case class MyGameData(score: Int,
                              insults: List[Insult],
                              comebacks: List[Comeback],
                              currentInsult: Option[Insult]) extends Data {
    def updateInsults(i: Insult) = this.copy(insults = i :: insults)
  }

}
