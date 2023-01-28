package io.lamedh.ziodd
package app.cli.sched
package boot

import zio.cli.HelpDoc.Span.text
import zio.cli.ZIOCliDefault
import zio.cli.CliApp
import zio.ZIO

object Main extends ZIOCliDefault {

  override def cliApp = CliApp.make(
    command = Commands.inv,
    name = "Sched",
    version = "0.0.7",
    summary = text("Explore subscription scheduler from cli!")
  ) { command =>
    ZIO
      .serviceWithZIO[CommandHandler](handler => handler.handle(command))
      .provide(layer)
  }
}
