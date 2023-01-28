package io.lamedh.ziodd.core.subscheduler

import zio.{IO, UIO, Task}
import zio.stream.Stream

trait SubscriptionSchedulerAlg {
  def get(invoiceId: String): IO[SchedError, Schedule]
  def save(entry: Schedule): IO[SchedError, Unit]
  def remove(invoiceId: String): IO[SchedError, Unit]

  def streamDueSchedule: Stream[SchedError, Schedule]
  def processOutstandingInvoices: UIO[Unit]
}
