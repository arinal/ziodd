import sbt._

object Dependencies {

  val zioVersion        = "2.0.6"
  val http4sVersion     = "0.23.18"
  val catsVersion       = "2.9.0"
  val pureConfigVersion = "0.17.1"
  val circeVersion      = "0.14.3"

  object Zio {
    val zio     = "dev.zio" %% "zio"              % zioVersion
    val prelude = "dev.zio" %% "zio-prelude"      % "1.0.0-RC16"
    val streams = "dev.zio" %% "zio-streams"      % zioVersion
    val cli     = "dev.zio" %% "zio-cli"          % "0.3.0-M02"
    val cats    = "dev.zio" %% "zio-interop-cats" % "23.0.0.0"
  }

  object Circe {
    val generic = "io.circe" %% "circe-generic" % circeVersion
  }

  object Config {
    val pureconfig = "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
  }

  object Logging {
    val zioLoggingSlf4j        = "dev.zio"       %% "zio-logging-slf4j" % "2.1.8"
    val slf4jApi               = "org.slf4j"      % "slf4j-api"         % "1.7.36"
    val logback                = "ch.qos.logback" % "logback-classic"   % "1.2.11"
    val logstashLogbackEncoder =
      "net.logstash.logback" % "logstash-logback-encoder" % "6.6"
  }

  object Http4s {
    val dsl         = "org.http4s" %% "http4s-dsl"          % http4sVersion
    val emberServer = "org.http4s" %% "http4s-ember-server" % http4sVersion
    val circe       = "org.http4s" %% "http4s-circe"        % http4sVersion
  }

  object layer {

    val core        = Seq(Zio.zio, Zio.prelude, Zio.streams)
    val appCliSched = Seq(Zio.cli, Config.pureconfig)

    val appApiHttpSched = Seq(
      Http4s.dsl,
      Http4s.emberServer,
      Http4s.circe,
      Logging.zioLoggingSlf4j,
      Logging.logback,
      Logging.slf4jApi,
      Logging.logstashLogbackEncoder,
      Circe.generic,
      Config.pureconfig,
      Zio.cats
    )
  }
}
