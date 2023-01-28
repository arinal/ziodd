package io.lamedh.ziodd
package app.api.http.sched
package boot

import infra.client.http.SubscriptionHttpClient
import infra.client.http.InvoiceHttpClient
import core.subscheduler.SubscriptionSchedulerLive
import pureconfig.ConfigSource
import pureconfig.ConfigReader
import com.comcast.ip4s.Port
import com.comcast.ip4s.Host
import zio.ZLayer
import zio.ZIO

case class Config(core: CoreConfig, infra: InfraConfig, app: AppConfig)
case class AppConfig(appApiHttpSched: App.Config)

case class InfraConfig(
    invoiceHttpClient: InvoiceHttpClient.Config,
    subscriptionHttpClient: SubscriptionHttpClient.Config
)

case class CoreConfig(subscheduler: SubscriptionSchedulerLive.Config)

object Config {

  import pureconfig.generic.auto._

  implicit val portReader = ConfigReader[Int].map(Port.fromInt(_).get)
  implicit val hostReader = ConfigReader[String].map(Host.fromString(_).get)

  val layer: ZLayer[
    Any,
    Nothing,
    SubscriptionSchedulerLive.Config
      with InvoiceHttpClient.Config
      with SubscriptionHttpClient.Config
      with App.Config
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

    val appApiHttpSched =
      ZLayer.fromFunction((conf: Config) => conf.app.appApiHttpSched)

    rootLayer >>> (coreSched ++ infraSubHttpClient ++ infraInvoiceHttpClient ++ appApiHttpSched)
  }
}
