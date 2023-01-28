package io.lamedh.ziodd
package app.api.http.sched

import java.time.OffsetDateTime

object model {

  final case class Schedule(
      invoiceId: String,
      due: OffsetDateTime,
      isTest: Boolean,
      partner: Option[String]
  )
}
