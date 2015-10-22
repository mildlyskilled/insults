package com.mildlyskilled.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.mildlyskilled.messages._

class GameEngine extends Actor with ActorLogging {

  def receive = {
    case Initialise => log.info("Starting game engine")
    case InsultMessage(target, insult) => {
      log.info("Forwarding insult")
      target ! InsultMessage(sender, insult)
    }

  }
}