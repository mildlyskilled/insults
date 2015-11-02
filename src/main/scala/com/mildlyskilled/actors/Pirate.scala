package com.mildlyskilled.actors

import com.mildlyskilled.messages.Protocol.InsultMessage
import com.mildlyskilled.models.{Insult, Comeback}

case class Pirate(override val knownInsults: List[Insult], override val knownComebacks: List[Comeback])
  extends Playable {

  var currentInsult: Insult = _

  override def handleSelectInsult(id: Int) = {
    currentInsult = scala.util.Random.shuffle(knownInsults).head
    sender() ! InsultMessage(knownInsults.head)
  }
}
