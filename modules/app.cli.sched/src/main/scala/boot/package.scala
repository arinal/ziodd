package io.lamedh.ziodd
package app.cli.sched

import zio.ZLayer

package object boot {

  val layer = ZLayer.make[CommandHandler](
    Config.layer,
    app.cli.sched.CommandHandler.layer,
    infra.memoryrepo.ScheduleMemoryRepo.layer(init = true),
    infra.client.http.InvoiceHttpClient.layer,
    infra.client.http.SubscriptionHttpClient.layer,
    core.subscheduler.SubscriptionSchedulerLive.layer
  )
}
