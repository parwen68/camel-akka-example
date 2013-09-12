package se.callista.akka.camel

import akka.camel.{Consumer, CamelMessage, Producer}
import akka.actor.{Props, ActorSystem, Actor}
import scala.concurrent.duration._
import util.parsing.json.{JSONObject, JSONArray}
import akka.util

class HttpProducer(host: String) extends Producer {
  import System._
  def endpointUri = "jetty://http://" + host + "/?bridgeEndpoint=true"

  def threadName = Thread.currentThread().getName

  var start : Long = 0

  override def transformOutgoingMessage(msg: Any) = {
    println("Request> " + threadName + " " + host)
    start = currentTimeMillis()
    msg
  }

  override def transformResponse(msg: Any): Any = msg match {
    case msg: CamelMessage => {
      val t = currentTimeMillis() - start
      println("Response> " + threadName + " " + host)
      msg.copy(headers = msg.headers ++ Map("Url" -> host, "RequestTime" -> t) )
    }
  }
}

class HttpConsumer(hosts: List[String]) extends Consumer {
  def endpointUri = "jetty://http://localhost:9090/"

  import scala.concurrent.Future
  import akka.pattern.ask

  val actors = hosts.map(host => context.actorOf(Props(new HttpProducer(host))))

  implicit val dispatcher = context.system.dispatcher
  implicit val timeout = util.Timeout(5 seconds)

  def receive = {
    case msg: CamelMessage => {
      val props = Map("CamelHttpMethod" -> "HEAD")
      val futures = actors.map(a => a.ask(CamelMessage("", props)).mapTo[CamelMessage])

      val r = Future.sequence(futures)
      val originalSender = sender
      r onSuccess  { case msg =>
        val headers = msg.map(_.headers(Set("Server", "Url", "RequestTime")))
        originalSender ! CamelMessage(new JSONArray( headers.map(new JSONObject(_))), Map("ContentType" -> "application/json"))
      }
      r onFailure { case msg =>
        originalSender ! CamelMessage(new JSONObject(Map("error" -> msg)), Map("ContentType" -> "application/json"))
      }
    }
  }
}

object Main extends App {

  val system = ActorSystem("some-system")

  val hosts = List(
    "www.aftonbladet.se",
    "www.expressen.se",
    "www.svd.se",
    "www.gp.se",
    "www.dn.se",
    "www.sydsvenskan.se",
    "www.corren.se",
    "www.barometern.se",
    "www.bt.se",
    "eposten.se",
    "www.helagotland.se",
    "www.gotland.net",
    "hallandsposten.se",
    "www.jonkopingsposten.se"
  )

  val consumer = system.actorOf(Props(new HttpConsumer(hosts)))

  println("Finished")
}
