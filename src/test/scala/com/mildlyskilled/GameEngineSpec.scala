package com.mildlyskilled

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import com.mildlyskilled.actors.{GameEngine, Player}
import com.mildlyskilled.messages.Protocol.{GoAway, Unregister, Registered, Register}
import com.mildlyskilled.models.Repo
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class GameEngineSpec(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender
with WordSpecLike
with Matchers
with BeforeAndAfterAll {

  def this() = this(ActorSystem("TestSpec"))

  val repo = new Repo
  val gameEngine = TestActorRef(Props(classOf[GameEngine], repo))
  val playerActor = TestActorRef(Props(classOf[Player], repo.getRandomInsults(2), repo.getRandomComebacks(2)))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A GameEngine actor" must {
    "send back a registered message on a register request" in {
      gameEngine ! Register(this.self)
      expectMsg(Registered)
    }
  }

  "A GameEngine actor" must {
    "Send back a GoAway message on receiving an unregister request" in {
      gameEngine ! Unregister(this.self)
      expectMsg(GoAway)
    }
  }

}
