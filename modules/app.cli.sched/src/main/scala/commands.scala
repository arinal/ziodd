package io.lamedh.ziodd.app.cli.sched

import zio.cli._

sealed trait Subcommand
final case class AddSchedule(invoiceId: String) extends Subcommand
final case class GetSchedule(invoiceId: String) extends Subcommand
final case object ListDue                       extends Subcommand

object Commands {

  val idFlag = Options.text("id")

  val add =
    Command("add", idFlag)
      .withHelp(HelpDoc.p("Adding a new schedule entry"))
      .map { case (id) => AddSchedule(id) }

  val get =
    Command("get", idFlag)
      .withHelp(HelpDoc.p("Get a schedule entry by invoice id"))
      .map { case (id) => GetSchedule(id) }

  val list =
    Command("list")
      .withHelp(HelpDoc.p("List all invoice due"))
      .map { _ => ListDue }

  val inv =
    Command("sched", Options.none, Args.none).subcommands(add, get, list)
}
