import play.core.PlayVersion.current
import sbt._


object AppDependencies {

  val hmrcMongoVersion = "2.1.0"
  val bootstrapVersion = "9.0.0"


  val compile = Seq(
      "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"          % hmrcMongoVersion,
      "uk.gov.hmrc"             %% "bootstrap-backend-play-30"   % bootstrapVersion,
      "com.beachape"            %% "enumeratum-play"             % "1.8.0"
    )

  val test = Seq(
    "org.scalatest"           %% "scalatest"                % "3.2.18"        ,
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"  % hmrcMongoVersion,
    "org.playframework"       %% "play-test"                % current         ,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "7.0.1"         ,
    "org.wiremock"             % "wiremock-standalone"      % "3.6.0"
  ).map(_ % Test)

}
