package com.mildlyskilled.actors

import akka.actor.{Actor, ActorLogging}
import com.mildlyskilled.messages.{InsultMessage, ComebackMessage, Leave}
import com.mildlyskilled.models.{Comeback, Insult}

class Player extends Actor with ActorLogging {

  def receive = {
    case InsultMessage(sender, Insult(id, message)) =>
      log.info(s"Player received insult $id")
    case ComebackMessage(sender, Comeback(id, message)) =>
      log.info(s"Player received comeback $id")
    case Leave => context.system.shutdown()
  }

}
