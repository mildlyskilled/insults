package com.mildlyskilled.actors

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import com.mildlyskilled.messages._
import com.mildlyskilled.models.{Repo, Entry}

class GameEngine extends Actor with ActorLogging {

  def receive = {
    case Initialise => log.info("Starting game engine")
    case InsultMessage(entry) => {
      log.info("Forwarding insult")
       InsultMessage(entry)
    }
    case KnownInsults(insults) => {
      println(s"---------- Known Insults from ${sender}")
      insults.foreach { i =>
        println(s"[${i.id}]: ${i.generalInsult}")
      }
    }

  }
}