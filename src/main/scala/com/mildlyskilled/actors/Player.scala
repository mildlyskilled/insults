package com.mildlyskilled.actors

import akka.actor.{Actor, ActorLogging}
import com.mildlyskilled.messages._
import com.mildlyskilled.models.Entry
import scala.collection.mutable
import scala.concurrent.Future

case class Player(override val knownInsults: List[Entry]) extends Playable
