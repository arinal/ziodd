package io.lamedh.ziodd.core.subscheduler

import zio.IO
import zio.stream.Stream
import java.time.OffsetDateTime

trait ScheduleRepo {
  
  def get(invoiceId: String): IO[SchedError, Schedule]
  def save(entry: Schedule): IO[SchedError, Unit]
  def remove(invoiceId: String): IO[SchedError, Unit]
  
  def streamSchedule(
      from: OffsetDateTime,
      to: OffsetDateTime
  ): Stream[SchedError, Schedule]
}
