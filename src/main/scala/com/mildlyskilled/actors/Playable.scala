package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor, FSM}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models._
import scala.concurrent.duration._


trait Playable extends Actor with ActorLogging with FSM[State, Data] {

  val knownInsults: List[Insult]
  val knownComebacks: List[Comeback]
  implicit val insults = knownInsults
  implicit val comebacks = knownComebacks

  startWith(Insulting, MyGameData(0, knownInsults, knownComebacks, None))

  when(Insulting) {

    case Event(Select(x), insultsSection @ MyGameData(_, playerInsults, _)) =>

      insults find { i => i.id == x } match {
        case Some(i) => {
          sender() ! InsultMessage(i)
          stay()
        }
        case None => {
          sender() ! Info("You do not know this insult")
          stay()
        }
      }

    case Event(InsultMessage(x), i@MyGameData(score, collectedInsults, collectedComebacks, currentInsult)) =>
      comebacks find (_.id == x.id) match {
        case None =>
          sender() ! ConcedeRound
          goto(Insulted) using i.copy(insults = x::collectedInsults, currentInsult = x)

        case Some(c) =>
          goto(Insulted) using i
      }


    case Event(Leave, _) =>
      log.warning("Shutting down")
      stop(FSM.Shutdown)


    case Event(e, s) =>
      log.warning("Received unknown message {} in State {}/{}", e, stateName, s)
      stay()


  }


  when(Insulted) {
    case Event(Select(c), Uninitialised) =>
      comebacks find {comeback => comeback.id == c } match {
        case Some(comeback) =>
          sender() ! ComebackMessage(comeback)
          stay using MyGameData(0, Nil, comeback :: Nil, None)

        case None =>
          sender() ! ConcedeRound
          stay()
      }
  }

  onTransition {
    case _ -> Insulting => insults foreach { i => println(s"[${i.id}] ${i.content}") }
    case Insulting -> Insulted => comebacks foreach { c => println(s"[${c.id}] ${c.content}") }
  }

  initialize()

}