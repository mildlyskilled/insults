package com.mildlyskilled.models

import play.api.libs.json.Json

import scala.io.Source

case class Entry(id: Int, bossInsult: String, generalInsult: String, comeback: String)

object Entry {
  implicit val reads = Json.reads[Entry]
}

case class Insult(id: Int, content: String)

case class Comeback(id: Int, content: String)

class Repo {
  val entries = {
    val rawInsults = Source.fromURI(getClass.getResource("/insults.json").toURI).mkString
    Json.parse(rawInsults).as[List[Entry]]
  }

  def getRandomInsults(count: Int):List[Insult] = scala.util.Random.shuffle(entries).take(count).map{ e =>
    Insult(e.id, e.generalInsult)
  }

  def getRandomComebacks(count: Int):List[Comeback] = scala.util.Random.shuffle(entries).take(count).map{ e =>
    Comeback(e.id, e.comeback)
  }

}