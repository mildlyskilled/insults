package com.mildlyskilled.models

import scala.io.Source
import spray.json._

case class Entry(id: Int, bossInsult: String, generalInsult: String, comeback: String)

case class Insult(id: Int, content: String)

case class Comeback(id: Int, content: String)

class Repo extends DefaultJsonProtocol {
  implicit val entryFormat = jsonFormat4(Entry)

  val entries =
    Source.fromURI(getClass.getResource("/insults.json").toURI)
      .mkString.parseJson.convertTo[List[Entry]]


  def getRandomInsults(count: Int):List[Insult] = scala.util.Random.shuffle(entries).take(count).map{ e =>
    Insult(e.id, e.generalInsult)
  }

  def getRandomComebacks(count: Int):List[Comeback] = scala.util.Random.shuffle(entries).take(count).map{ e =>
    Comeback(e.id, e.comeback)
  }

}