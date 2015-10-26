package com.mildlyskilled.actors

import akka.actor.ActorLogging
import com.mildlyskilled.models.Entry

class Pirate(val insults:List[Entry], var current:Option[Entry]) extends Player(insults, current) with ActorLogging {

}
