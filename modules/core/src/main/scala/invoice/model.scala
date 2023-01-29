package io.lamedh.ziodd.core.invoice

import zio.prelude.NonEmptyList

sealed trait Invoice {
  def isBillable: Boolean
}

final case class PreBilledInvoice(lineItems: NonEmptyList[LineItem])
    extends Invoice {
  override def isBillable = lineItems.exists(!_.cancelled)
}

final case class LineItem(subscriptionId: String, cancelled: Boolean)

final case object BilledInvoice extends Invoice {
  override def isBillable = false
}
