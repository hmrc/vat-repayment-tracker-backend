import sbt._


object AppDependencies {

  val hmrcMongoVersion = "2.3.0"
  val bootstrapVersion = "9.5.0"


  val compile = Seq(
      "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"          % hmrcMongoVersion,
      "uk.gov.hmrc"             %% "bootstrap-backend-play-30"   % bootstrapVersion,
      "com.beachape"            %% "enumeratum-play"             % "1.8.2"
    )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"  % hmrcMongoVersion,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "7.0.1"         ,
    "org.wiremock"             % "wiremock-standalone"      % "3.10.0"
  ).map(_ % Test)

}
