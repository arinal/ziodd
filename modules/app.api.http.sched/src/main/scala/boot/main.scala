package io.lamedh.ziodd
package app.api.http.sched
package boot

import zio.interop.catz._
import zio._

object Main extends ZIOAppDefault {
  
  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {
    ZIO
      .serviceWithZIO[App] { api =>
        api.serverResource.useForever
      }
      .provideLayer(layer)
  }
}
