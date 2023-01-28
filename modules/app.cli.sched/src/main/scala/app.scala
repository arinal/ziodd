package io.lamedh.ziodd
package app.cli.sched

import core.subscheduler.SubscriptionSchedulerAlg
import core.subscheduler.Schedule
import core.subscheduler.InvalidDomain
import zio.Clock
import zio.ZIO
import zio.Console.printLine
import zio.ZLayer

class CommandHandler(schedAlg: SubscriptionSchedulerAlg) {

  def handle(command: Subcommand) =
    (
      command match {
        case AddSchedule(id) => addSchedule(id)
        case GetSchedule(id) => getSchedule(id)
        case ListDue         => listDue
      }
    ).catchAll { case InvalidDomain(errs) =>
      ZIO.foreach(errs)(msg => printLine(s"Error: $msg"))
    }

  def listDue =
    for {
      ents <- schedAlg.streamDueSchedule.runCollect
      _    <- printLine("Printing all due schedules")
      _    <- printLine("==========================")
      _    <- ZIO.foreach(ents)(e => printLine(e.toString))
    } yield ()

  def getSchedule(id: String) =
    for {
      sched <- schedAlg.get(id)
      _     <- printLine(s"Found schedule with invoice id: $id")
      _     <- printLine(sched.toString)
    } yield ()

  def addSchedule(id: String) =
    for {
      now      <- Clock.currentDateTime
      schedule <- Schedule.safe(id, now, true, None)
      _        <- schedAlg.save(schedule)
      _        <- printLine("Schedule added")
    } yield ()
}

object CommandHandler {
  val layer = ZLayer.fromFunction(new CommandHandler(_))
}
