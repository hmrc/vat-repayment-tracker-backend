import play.core.PlayVersion.current
import sbt._


object AppDependencies {

  val hmrcMongoVersion = "1.3.0"
  val bootstrapVersion = "7.22.0"
  val jacksonVersion = "2.13.2"
  val jacksonDatabindVersion = "2.13.2.2"

  val jacksonOverrides = Seq(
    // format: OFF
    "com.fasterxml.jackson.core" % "jackson-core",
    "com.fasterxml.jackson.core" % "jackson-annotations",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310"
    // format: ON
  ).map(_ % jacksonVersion)

  val jacksonDatabindOverrides = Seq(
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonDatabindVersion
  )

  val akkaSerializationJacksonOverrides = Seq(
    // format: OFF
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor",
    "com.fasterxml.jackson.module" % "jackson-module-parameter-names",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"
    // format: ON
  ).map(_ % jacksonVersion)

  val compile =  {
    val dependencies = Seq(
      "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"          % hmrcMongoVersion,
      "uk.gov.hmrc"             %% "bootstrap-backend-play-28"   % bootstrapVersion,
      "com.beachape"            %% "enumeratum-play"             % "1.7.2"
    )
    dependencies ++ jacksonDatabindOverrides ++ jacksonOverrides ++ akkaSerializationJacksonOverrides
  }

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.2.17"                % Test,
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % bootstrapVersion        % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"  % hmrcMongoVersion        % Test,
    "com.vladsch.flexmark"     % "flexmark-all"             % "0.64.6"                % "test, it",
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"                 % Test,
    "org.wiremock"             % "wiremock-standalone"      % "3.0.3"                % Test
  )

}
