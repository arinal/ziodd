package io.lamedh.ziodd
package infra.client.http

import core.invoice._
import zio._

class InvoiceHttpClient(config: InvoiceHttpClient.Config) extends InvoiceAlg {

  override def createInvoice(invoice: Invoice): IO[InvoiceError, Invoice] =
    ZIO.succeed(BilledInvoice)

  override def getInvoice(invoiceId: String): IO[InvoiceError, Invoice] =
    for {
      _ <- ZIO.log(s"Calling ${config.url}")
    } yield BilledInvoice
}

object InvoiceHttpClient {

  case class Config(url: String)

  val layer = ZLayer.fromFunction(new InvoiceHttpClient(_))
}
