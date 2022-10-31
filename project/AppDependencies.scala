import play.core.PlayVersion.current
import sbt._


object AppDependencies {

  val hmrcMongoVersion = "0.73.0"

  val compile = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"          % hmrcMongoVersion,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"   % "5.4.0",
    "com.beachape"            %% "enumeratum-play"             % "1.5.13"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.2.9"                 % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"  % hmrcMongoVersion        % Test,
    "com.vladsch.flexmark"     % "flexmark-all"             % "0.35.10"               % "test, it",
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"                 % Test,
    "com.github.tomakehurst"   % "wiremock-jre8"            % "2.21.0"                % Test
  )
}
