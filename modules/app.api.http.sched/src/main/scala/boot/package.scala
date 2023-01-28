package io.lamedh.ziodd
package app.api.http.sched

import zio.ZLayer

package object boot {

  val layer = ZLayer.make[app.api.http.sched.App](
    boot.Config.layer,
    app.api.http.sched.App.layer,
    infra.memoryrepo.ScheduleMemoryRepo.layer(init = true),
    infra.client.http.InvoiceHttpClient.layer,
    infra.client.http.SubscriptionHttpClient.layer,
    core.subscheduler.SubscriptionSchedulerLive.layer
  )
}
