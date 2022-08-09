package application

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import application.domain.greeter.{Greeter, PersonalGreeter}
import application.domain.sequence.Sequence
import library.MathLibrary

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    println(s"Greeter.hello = ${Greeter.hello}")
    println(s"PersonalGreeter.hello(Bazel) = ${PersonalGreeter.hello("Bazel")}")
    println(s"MathLibrary.add(1, 2) = ${MathLibrary.add(1, 2)}")
    println(s"MathLibrary.circumference(2) = ${MathLibrary.circumference(2)}")
    println(s"Sequence.generate(0,10) = ${Sequence.generate(0, 10)}")

    implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
