package io.lamedh.ziodd.core
package subscheduler

import common.fail
import zio.prelude.Validation.{validateWith, succeed}
import zio.IO

import java.time.OffsetDateTime

final case class Schedule(
    invoiceId: String,
    due: OffsetDateTime,
    isTest: Boolean,
    partner: Option[String]
)

object Schedule {

  def safe(
      invoiceId: String,
      due: OffsetDateTime,
      isTest: Boolean = false,
      partner: Option[String] = None
  ): IO[SchedError, Schedule] = {
    validateWith(
      validateInvoiceId(invoiceId),
      nonCompetitor(partner)
    ) { (_, _) =>
      Schedule(invoiceId, due, isTest, partner)
    }.toZIOAssociative
      .mapError(InvalidDomain)
  }

  private def validateInvoiceId(id: String) =
    if (id.isEmpty) fail("invoiceId couldn't be empty")
    else if (id.size > 5) fail("invoiceId too long")
    else succeed(id)

  private def nonCompetitor(partner: Option[String]) =
    if (partner == Some("competitor")) fail("Never partnering with competitor")
    else succeed(partner)
}
