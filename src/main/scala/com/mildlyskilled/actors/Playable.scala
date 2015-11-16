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

  startWith(Insulting, Uninitialised)

  when(Insulting) {

    case Event(Select(x), Uninitialised) => {

      insults foreach { i => println(s"[${i.id}] ${i.content}") }

      insults find { i => i.id == x } match {
        case Some(i) => {
          sender() ! InsultMessage(i)
          stay using MyGameData(0, i :: Nil, Nil)
        }
        case None => sender() ! Info("You do not know this insult")
      }
      stay()
    }


    case Event(InsultMessage(x), i@MyGameData(_, collectedInsults, _)) => {
      comebacks find (_.id == x.id) match {
        case None => {
          sender() ! ConcedeRound
          goto(Insulted) using i.copy(insults = x :: collectedInsults)
        }
        case Some(c) => {
          sender() ! ComebackMessage(c)
          goto(Insulted) using i
        }
      }
    }

    case Event(Leave, _) => {
      log.warning("Shutting down")
      stop(FSM.Shutdown)
    }

    case Event(e, s) => {
      log.warning("received unhandled request {} in State {}/{}", e, stateName, s)
      stay()
    }

  }


  onTransition {
    case _ -> Insulting => insults foreach { i => println(s"[${i.id}] ${i.content}") }
    case Insulting -> Insulted => comebacks foreach { c => println(s"[${c.id}] ${c.content}") }
  }

  initialize()

}