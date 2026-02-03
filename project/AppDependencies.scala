import sbt.*


object AppDependencies {
  val hmrcMongoVersion = "2.12.0"
  val bootstrapVersion = "10.5.0"

  val compile: Seq[ModuleID] = Seq(
      "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"          % hmrcMongoVersion,
      "uk.gov.hmrc"             %% "bootstrap-backend-play-30"   % bootstrapVersion,
      "com.beachape"            %% "enumeratum-play"             % "1.9.4"
    )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"  % hmrcMongoVersion,
    "org.wiremock"             % "wiremock-standalone"      % "3.13.2"
  ).map(_ % Test)
}
