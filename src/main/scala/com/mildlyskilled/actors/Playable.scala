package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor, FSM}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models._
import scala.concurrent.duration._


trait Playable extends Actor with ActorLogging with FSM[State, Data]{

  val knownInsults: List[Insult]
  val knownComebacks: List[Comeback]
  implicit val insults = knownInsults
  implicit val comebacks = knownComebacks

  startWith(Insulting, Uninitialised)

  when(Insulting) {

    case Event(Select(x), Uninitialised) => {
      insults find { i => i.id == x } match {
        case Some(i) => {
          sender() ! InsultMessage(i)
          stay using MyGameData(0, i :: Nil, Nil)
        }
        case None => Info("You do not know this insult")
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


  }

  initialize()

  def receiveCommand: Receive = {
    case Info(msg) => log.info(Console.YELLOW + msg + Console.RESET)

    case InsultMessage(insult) => handleInsult(insult)

    case ComebackMessage(comeback) => handleComeback(comeback)

    case GetInsults => handleReturnInsults()

    case GetComebacks => handleReturnComebacks()

    case SelectInsult(id) => handleSelectInsult(id)

    case ConcedeGame => handleConcedeGame()

    case GoAway => handleGoAway()

    case Leave => context.system.shutdown()

    case Registered => handleRegisteredMessage()

    case Select(x) => log.info("MESSAGE")
  }

  def handleInsult(i: Insult) (implicit comebacks: List[Comeback]) = {
    log.info(Console.GREEN + s"${i.content}" + Console.RESET)
    comebacks find (_.id == i.id) match {
      case None => sender() ! ConcedeRound
      case Some(c) => sender() ! ComebackMessage(c)
    }
  }

  def handleComeback(c: Comeback) = {
    log.info(Console.GREEN + s"${c.content}" + Console.RESET)
    sender() ! ConcedeRound
  }

  def handleReturnInsults() = {
    sender() ! KnownInsults(knownInsults)
  }

  def handleSelectInsult(id: Int)(implicit insults: List[Insult]) = {
    println(insults)

  }

  def handleGoAway() = {
    log.info(Console.RED + s"${self.path.name} leaving now" + Console.RESET)
    context stop self
  }

  def handleConcedeGame() = {
    log.info(Console.RED + s"I have lost ${self.path.name} concedes" + Console.RESET)
    context stop self
  }

  def handleReturnComebacks() = {
    sender() ! KnownComebacks(knownComebacks)
  }

  def handleRegisteredMessage() = {
    sender() ! ReadyToEngage
  }

}