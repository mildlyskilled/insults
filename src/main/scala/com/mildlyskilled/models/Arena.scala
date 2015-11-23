package com.mildlyskilled.models

import akka.actor.ActorRef
import scala.collection.mutable

case class Arena(name: String, players: mutable.Map[ActorRef, Int]) {

  val playerLimit = 2
  val scoreLimit = 2
  val defaultScore = 0

  def addPlayer(player: ActorRef) =
    if (!players.contains(player) && (players.size < playerLimit)) players += (player -> defaultScore)

  def removePlayer(player: ActorRef) = if (players.contains(player)) players -= player

  def incrementScore(player: ActorRef) =
    if (players.contains(player) && (players(player) < scoreLimit)) players(player) += 1

  def resetScore(player: ActorRef) = if (players.contains(player)) players(player) = defaultScore

  def getPlayerScore(player: ActorRef): Int = players(player)

  def getPlayers = players.keys

}
