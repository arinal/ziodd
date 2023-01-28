package io.lamedh.ziodd.core.subscheduler

trait SchedError
case class ScheduleNotFound(invoiceId: String)   extends SchedError
case class InvalidDomain(messages: List[String]) extends SchedError
