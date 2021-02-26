import play.core.PlayVersion.current
import sbt._

object AppDependencies {
  val compile = Seq(
    "uk.gov.hmrc"             %% "simple-reactivemongo"     % "7.30.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.14.0",
    "com.beachape"            %% "enumeratum-play"          % "1.5.13"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.8"                 % Test,
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.2"                 % Test,
    "com.github.tomakehurst"  % "wiremock-jre8"             % "2.21.0"                % Test
  )
}
