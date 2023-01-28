package io.lamedh.ziodd
package app.api.http.sched

import core.subscheduler._
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.Port
import com.comcast.ip4s.Host
import zio.interop.catz._
import zio.{Task, ZLayer, ZIO}

class App(schedAlg: SubscriptionSchedulerAlg, config: App.Config) {

  val dsl = Http4sDsl[Task]

  import dsl._
  import org.http4s._
  import org.http4s.circe._
  import org.http4s.circe.CirceEntityCodec._
  import io.circe.generic.auto._

  implicit val decoder = jsonOf[Task, model.Schedule]

  val routes = HttpRoutes
    .of[Task] {
      case GET -> Root / "schedule" / invoiceId =>
        schedAlg
          .get(invoiceId)
          .flatMap(inv => Ok(inv))
          .catchAll { case ScheduleNotFound(id) =>
            ZIO.log(s"Schedule not found for invoice $id") *> NotFound()
          }

      case req @ POST -> Root / "schedule" =>
        val op = for {
          req   <- req.as[model.Schedule]
          sched <-
            Schedule.safe(req.invoiceId, req.due, req.isTest, req.partner)
          _     <- schedAlg.save(sched)
          _     <- ZIO.log(s"Saved schedule for invoice ${req.invoiceId}")
          resp  <- Created()
        } yield resp
        op.catchAll {
          case InvalidDomain(errs) =>
            ZIO.foreach(errs)(ZIO.logError(_)) *> BadRequest()
          case err: Throwable      =>
            ZIO.logError(err.getMessage) *> InternalServerError()
        }
    }
    .orNotFound

  val serverResource =
    EmberServerBuilder
      .default[Task]
      .withHttpApp(routes)
      .withHost(config.host)
      .withPort(config.port)
      .build
}

object App {

  case class Config(host: Host, port: Port)

  val layer =
    ZLayer.fromFunction((cfg: Config, alg: SubscriptionSchedulerAlg) =>
      new App(alg, cfg)
    )
}
