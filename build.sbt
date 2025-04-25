import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import wartremover.Wart
import sbt.Keys.*
import play.sbt.PlayImport.PlayKeys

val appName = "vat-repayment-tracker-backend"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    scalaVersion                     := "2.13.16",
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    retrieveManaged                  :=  true,
    routesGenerator                  :=  InjectedRoutesGenerator
  )
  .settings(SbtUpdatesSettings.sbtUpdatesSettings)
  .settings(majorVersion := 1)
  .settings(ScalariformSettings())
  .settings(ScoverageSettings())
  .settings(WartRemoverSettings.wartRemoverError)
  .settings(WartRemoverSettings.wartRemoverWarning)
  .settings(Test / compile / wartremover.WartRemover.autoImport.wartremoverErrors --= Seq(Wart.Any, Wart.Equals, Wart.Null, Wart.NonUnitStatements, Wart.PublicInference))
  .settings(wartremover.WartRemover.autoImport.wartremoverExcluded ++=
    (Compile / routes).value ++
      (baseDirectory.value / "test").get ++
      Seq(sourceManaged.value / "main" / "sbt-buildinfo" / "BuildInfo.scala"))
  .settings(PlayKeys.playDefaultPort := 9212)
  .settings(scalaSettings *)
  .settings(defaultSettings() *)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(
    routesImport ++= Seq(
       "model.Vrn",
       "model.PeriodKey"
    ))
  .settings(
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-Xlint:-missing-interpolator,_",
      "-Xlint:adapted-args",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Ywarn-value-discard",
      "-Ywarn-dead-code",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      // required in place of silencer plugin
      "-Wconf:cat=unused-imports&src=html/.*:s",
      "-Wconf:src=routes/.*:s"
    )
  )
  .settings(Compile / scalacOptions -= "utf8")
  .settings(
    commands += Command.command("runTestOnly") { state =>
      state.globalLogging.full.info("running play using 'testOnlyDoNotUseInAppConf' routes...")
      s"""set javaOptions += "-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"""" ::
        "run" ::
        s"""set javaOptions -= "-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"""" ::
        state
    }
  )
