package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor}
import com.mildlyskilled.messages.Protocol._
import com.mildlyskilled.models.Entry


trait Playable extends Actor with ActorLogging{

  val knownInsults: List[Entry]

  def receive = {
    case InsultMessage(entry) => {
      if (knownInsults.takeWhile(entry.id == _.id).isEmpty) {
       sender() ! ConcedeRound
      }
      log.info(s"I received insult ${entry.id} from ${sender.path.name}")
    }
    case ComebackMessage(entry) => log.info(s"Player received comeback ${entry.id}")
    case GetInsults => sender() ! KnownInsults(knownInsults)
    case SelectInsult(id) => sender() ! InsultMessage(knownInsults.filter(_.id == id).head)
    case ConcedeGame => {
      log.info(s"I have lost ${self.path.name} concedes")
    }
    case Leave => context.system.shutdown()
  }
}
