package com.mildlyskilled.models

import akka.actor.ActorRef

case class Arena(name: String, players:scala.collection.mutable.Map[ActorRef, Int]) {

  val playerLimit = 2
  val scoreLimit = 2

  def addPlayer(player: ActorRef) = {
    if (!players.keys.exists(p => p == player) && (players.size < playerLimit)){
      players += (player -> 2)
    }
  }

  def addScore(player: ActorRef) = {
    if (players(player) < scoreLimit)
      if (!players.keys.exists(p => p == player)) {
        players(player) += 1
      }
  }


  def getPlayers = {
    players.keys
  }

  def removePlayer(player: ActorRef) = {
    if(players.keys.exists(p => p == player))
      players -= player
  }
}
