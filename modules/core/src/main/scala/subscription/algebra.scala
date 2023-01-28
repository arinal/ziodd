package io.lamedh.ziodd.core.subscription

import zio.IO

trait SubscriptionAlg {
  def revoke(
      subscriptionId: String,
      isTest: Boolean = false,
      partner: Option[String] = None
  ): IO[SubscriptionError, Unit]
}
