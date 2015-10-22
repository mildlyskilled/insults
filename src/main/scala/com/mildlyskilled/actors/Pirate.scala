package com.mildlyskilled.actors

import akka.actor.{ActorLogging, Actor}
import com.mildlyskilled.models.{Comeback, Insult}

class Pirate extends Actor with ActorLogging {
  val knownInsults: List[Insult] = ???
  val knowComebacks: List[Comeback] = ???

  def receive = {
    case Insult(id, message) =>
      log.info(message)
    // send comeback using ID
  }

}
