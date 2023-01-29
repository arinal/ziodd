package io.lamedh.ziodd.core

import zio.NonEmptyChunk
import zio.prelude.Validation

object common {

  type Result[A] = Validation[NonEmptyChunk[String], A]

  def fail(error: String) = Validation.fail(NonEmptyChunk(error))
}
