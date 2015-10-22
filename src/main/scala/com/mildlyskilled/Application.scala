package com.mildlyskilled

import akka.actor.ActorSystem


object Application extends App {
  val system = ActorSystem("InsultSystem")
}
