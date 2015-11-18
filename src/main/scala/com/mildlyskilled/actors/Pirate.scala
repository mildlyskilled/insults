package com.mildlyskilled.actors

import com.mildlyskilled.messages.Protocol.{WaitingForEngagement}
import com.mildlyskilled.models.{Insult, Comeback}

case class Pirate(override val knownInsults: List[Insult], override val knownComebacks: List[Comeback])
  extends Playable

