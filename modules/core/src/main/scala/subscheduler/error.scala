package io.lamedh.ziodd.core.subscheduler

import zio.NonEmptyChunk

trait SchedError
case class ScheduleNotFound(invoiceId: String)            extends SchedError
case class InvalidDomain(messages: NonEmptyChunk[String]) extends SchedError
