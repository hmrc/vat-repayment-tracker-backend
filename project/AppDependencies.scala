import play.core.PlayVersion.current
import sbt._


object AppDependencies {

  val hmrcMongoVersion = "1.3.0"
  val bootstrapVersion = "7.22.0"
  val jacksonVersion = "2.15.2"

  // Provides newer jackson-databind than provided by play to avoid CVE-2020-36518 (MDTP Platform Catalogue id) vulnerability
  val jacksonOverride = Seq(
    // format: OFF
    "com.fasterxml.jackson.module" %% "jackson-module-scala"
    // format: ON
  ).map(_ % jacksonVersion)

  val compile =  {
    val dependencies = Seq(
      "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"          % hmrcMongoVersion,
      "uk.gov.hmrc"             %% "bootstrap-backend-play-28"   % bootstrapVersion,
      "com.beachape"            %% "enumeratum-play"             % "1.7.2",
    )
    dependencies ++
//      jacksonDatabindOverrides ++
//      jacksonOverrides ++
      jacksonOverride
  }

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.2.17"                % Test,
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % bootstrapVersion        % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"  % hmrcMongoVersion        % Test,
    "com.vladsch.flexmark"     % "flexmark-all"             % "0.64.6"                % "test, it",
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"                 % Test,
    "org.wiremock"             % "wiremock-standalone"      % "3.2.0"                % Test
  )

}
