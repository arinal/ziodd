package io.lamedh.ziodd
package app.cli.sched.boot

import infra.client.http.SubscriptionHttpClient
import infra.client.http.InvoiceHttpClient
import core.subscheduler.SubscriptionSchedulerLive
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.ZLayer
import zio.ZIO

case class Config(core: CoreConfig, infra: InfraConfig)

case class InfraConfig(
    invoiceHttpClient: InvoiceHttpClient.Config,
    subscriptionHttpClient: SubscriptionHttpClient.Config
)

case class CoreConfig(subscheduler: SubscriptionSchedulerLive.Config)

object Config {

  val layer: ZLayer[
    Any,
    Nothing,
    SubscriptionSchedulerLive.Config
      with InvoiceHttpClient.Config
      with SubscriptionHttpClient.Config
  ] = {
    val rootLayer = ZLayer.fromZIO {
      ZIO
        .fromEither(ConfigSource.default.load[Config])
        .orDieWith(err =>
          new IllegalStateException(s"Error loading configuration: $err")
        )
    }

    val coreSched =
      ZLayer.fromFunction((conf: Config) => conf.core.subscheduler)

    val infraInvoiceHttpClient =
      ZLayer.fromFunction((conf: Config) => conf.infra.invoiceHttpClient)

    val infraSubHttpClient =
      ZLayer.fromFunction((conf: Config) => conf.infra.subscriptionHttpClient)

    rootLayer >>> (coreSched ++ infraInvoiceHttpClient ++ infraSubHttpClient)
  }
}
