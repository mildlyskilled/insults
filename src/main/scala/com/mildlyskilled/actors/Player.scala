package com.mildlyskilled.actors

import akka.actor.{Actor, ActorLogging}
import com.mildlyskilled.messages._
import com.mildlyskilled.models.Entry
import scala.collection.mutable
import scala.concurrent.Future

class Player(val knownInsults: List[Entry], var currentInsult: Option[Entry]) extends Actor with ActorLogging {

  def receive = {
    case InsultMessage(entry) => log.info(s"Player received insult ${entry.id}")
    case ComebackMessage(entry) => log.info(s"Player received comeback ${entry.id}")
    case GetInsults => knownInsults.foreach { i => println(s"[${i.id}] ${i.generalInsult}") }
    case SelectInsult(id) => {
      val insult = knownInsults.filter(_.id == id).head
      currentInsult = Some(insult)
      println(s"Current insult: ${insult.generalInsult}")
    }
    case Leave => context.system.shutdown()
  }
}
