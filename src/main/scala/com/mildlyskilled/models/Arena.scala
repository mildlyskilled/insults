package com.mildlyskilled.models

import akka.actor.ActorRef
import scala.collection.mutable

case class Arena(
                  name: String,
                  players: mutable.Map[ActorRef, Int],
                  gameStats: mutable.Map[String, Int] = mutable.Map("wins" -> 0, "losses" -> 0)
                ) {

  val playerLimit = 2
  val scoreLimit = 2
  val defaultScore = 0
  var seenInsults = Seq.empty[Insult]

  def addPlayer(player: ActorRef) =
    if (!players.contains(player) && (players.size < playerLimit)) {
      players += (player -> defaultScore)
    }

  def removePlayer(player: ActorRef) = if (players.contains(player)) players -= player

  def incrementScore(player: ActorRef) =
    if (players.contains(player) && (players(player) < scoreLimit)) players(player) += 1

  def resetScore(player: ActorRef) = if (players.contains(player)) players(player) = defaultScore

  def getPlayerScore(player: ActorRef): Int = players(player)

  def addToWins() = gameStats("wins") += 1

  def addToLosses() = gameStats("losses") += 1

  def addToInsults(insult: Insult) = insult +: seenInsults

  def getPlayers = players.keys

}
