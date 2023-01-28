package io.lamedh.ziodd.core.invoice

import zio.IO

trait InvoiceAlg {
  def createInvoice(invoice: Invoice): IO[InvoiceError, Invoice]
  def getInvoice(invoiceId: String): IO[InvoiceError, Invoice]
}
