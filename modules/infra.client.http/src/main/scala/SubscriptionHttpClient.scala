package io.lamedh.ziodd
package infra.client.http

import core.subscription.SubscriptionAlg
import core.subscription.SubscriptionError
import zio.IO
import zio.ZIO
import zio.ZLayer

class SubscriptionHttpClient(config: SubscriptionHttpClient.Config)
    extends SubscriptionAlg {
      
  def revoke(
      subscriptionId: String,
      isTest: Boolean = false,
      partner: Option[String] = None
  ): IO[SubscriptionError, Unit] =
    ZIO.log(s"Revoking $subscriptionId to ${config.url}")
}

object SubscriptionHttpClient {

  case class Config(url: String)

  val layer: ZLayer[Config, Nothing, SubscriptionAlg] =
    ZLayer.fromFunction(new SubscriptionHttpClient(_))
}
