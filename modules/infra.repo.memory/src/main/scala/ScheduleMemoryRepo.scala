package io.lamedh.ziodd
package infra.memoryrepo

import core.subscheduler._
import zio.stream.Stream
import zio.stream.ZStream
import zio.{IO, ZLayer, Ref, ZIO}
import zio.Clock
import java.time.OffsetDateTime

class ScheduleMemoryRepo(ref: Ref[Map[String, Schedule]]) extends ScheduleRepo {

  override def get(invoiceId: String): IO[SchedError, Schedule] =
    for {
      map   <- ref.get
      sched <- ZIO
                 .fromOption(map.get(invoiceId))
                 .mapError(_ => ScheduleNotFound(invoiceId))
    } yield sched

  override def save(entry: Schedule): IO[SchedError, Unit] =
    for {
      _ <- ZIO.logDebug(s"Persisting $entry into memory")
      _ <- ref.update(m => m + (entry.invoiceId -> entry))
    } yield ()

  override def remove(invoiceId: String): IO[SchedError, Unit] =
    for {
      _ <- ZIO.logDebug(s"Removing $invoiceId from memory")
      _ <- ref.update(m => m - invoiceId)
    } yield ()

  override def streamSchedule(
      from: OffsetDateTime,
      to: OffsetDateTime
  ): Stream[SchedError, Schedule] = {
    for {
      map    <- ZStream.fromZIO(ref.get)
      ordered = map.values.toList
                  .sortBy(_.due)
                  .dropWhile(_.due.isBefore(from))
                  .takeWhile(_.due.isBefore(to))
      entry  <- ZStream.fromIterable(ordered)
    } yield entry
  }
}

object ScheduleMemoryRepo {

  def layer(init: Boolean): ZLayer[Any, Nothing, ScheduleRepo] =
    ZLayer {
      for {
        now <- Clock.currentDateTime
        map  = if (init) initialMap(now) else Map.empty[String, Schedule]
        ref <- Ref.make(map)
      } yield new ScheduleMemoryRepo(ref)
    }

  private def initialMap(now: OffsetDateTime) = Map(
    "INV1" -> Schedule("INV1", now.minusDays(8), true, None),
    "INV2" -> Schedule("INV2", now.minusDays(8), true, None),
    "INV3" -> Schedule("INV3", now, true, None),
    "INV4" -> Schedule("INV4", now, true, None),
    "INV5" -> Schedule("INV5", now.plusDays(1), true, None),
    "INV6" -> Schedule("INV6", now.plusDays(2), true, None),
    "INV7" -> Schedule("INV7", now.minusDays(5), true, None),
    "INV8" -> Schedule("INV8", now.minusDays(4), true, None)
  )
}
