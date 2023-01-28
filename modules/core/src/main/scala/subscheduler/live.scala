package io.lamedh.ziodd
package core.subscheduler

import core.invoice._
import core.subscription.SubscriptionAlg
import zio.stream.{ZStream, Stream}
import zio.{Clock, IO, UIO, Task, ZIO, ZLayer}

class SubscriptionSchedulerLive(
    invoiceAlg: InvoiceAlg,
    subcriptionAlg: SubscriptionAlg,
    entryRepo: ScheduleRepo,
    config: SubscriptionSchedulerLive.Config
) extends SubscriptionSchedulerAlg {

  override def get(invoiceId: String): IO[SchedError, Schedule] =
    entryRepo.get(invoiceId)

  override def save(entry: Schedule): IO[SchedError, Unit] =
    for {
      _ <- ZIO.logDebug(s"Persisting $entry")
      _ <- entryRepo.save(entry)
    } yield ()

  override def remove(invoiceId: String): IO[SchedError, Unit] =
    for {
      _ <- ZIO.logDebug(s"Removing $invoiceId")
      _ <- entryRepo.remove(invoiceId)
    } yield ()

  override def streamDueSchedule: Stream[SchedError, Schedule] =
    for {
      dueTime <- ZStream.fromZIO(Clock.currentDateTime)
      from     = dueTime.minusDays(config.maxDaysInThePast)
      stream  <- entryRepo.streamSchedule(from, dueTime)
    } yield (stream)

  override def processOutstandingInvoices: UIO[Unit] =
    streamDueSchedule
      .mapZIOPar(10)(getInvoiceAndRevokeCancelled)
      .catchAll(e =>
        ZStream.logError(s"Error processing outstanding invoices: $e")
      )
      .runDrain

  private def getInvoiceAndRevokeCancelled(entry: Schedule): UIO[Unit] = {
    invoiceAlg
      .getInvoice(entry.invoiceId)
      .tap(revokeLineItems(entry, _))
      .catchAll(e => ZIO.logError(e.message))
      .unit
  }

  private def revokeLineItems(entry: Schedule, invoice: Invoice): UIO[Unit] = {
    invoice match {
      case PreBilledInvoice(lineItems) =>
        ZIO
          .foreach(lineItems.toList) { lineItem =>
            ZIO.when(lineItem.cancelled)(
              subcriptionAlg
                .revoke(
                  lineItem.subscriptionId,
                  entry.isTest,
                  entry.partner
                )
            )
          }
          .catchAll(e => ZIO.logError(e.message))
          .unit

      case BilledInvoice => ZIO.unit
    }
  }
}

object SubscriptionSchedulerLive {

  case class Config(maxDaysInThePast: Int)

  def layer: ZLayer[
    InvoiceAlg with SubscriptionAlg with ScheduleRepo with Config,
    Nothing,
    SubscriptionSchedulerAlg
  ] =
    ZLayer.fromFunction {
      (
          inv: InvoiceAlg,
          sub: SubscriptionAlg,
          rep: ScheduleRepo,
          conf: Config
      ) => new SubscriptionSchedulerLive(inv, sub, rep, conf)
    }
}
