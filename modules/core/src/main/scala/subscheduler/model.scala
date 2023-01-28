package io.lamedh.ziodd.core
package subscheduler

import zio.IO
import cats.data._
import cats.syntax.all._

import java.time.OffsetDateTime
import zio.ZIO

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
    val res = validateInvoiceId(invoiceId)
      .map(id => Schedule(id, due, isTest, partner))
    validatedToIO(res)
  }

  private def validateInvoiceId(id: String): Result[String] =
    if (id.isEmpty) s"invoiceId couldn't be empty".invalidNec
    else if (id.size > 5) "invoiceId too long".invalidNec
    else id.validNec

  type Result[A] = ValidatedNec[String, A]

  private def validatedToIO[A](result: Result[A]): IO[InvalidDomain, A] = {
    ZIO.fromEither(result.toEither.left.map(errs => InvalidDomain(errs.toList)))
  }
}
