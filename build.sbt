
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import wartremover.Wart

val appName = "vat-repayment-tracker-backend"

scalaVersion := "2.12.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    retrieveManaged                  :=  true,
    routesGenerator                  :=  InjectedRoutesGenerator,
    evictionWarningOptions in update :=  EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
  )
  .settings(majorVersion := 1)
  .settings(ScalariformSettings())
  .settings(ScoverageSettings())
  .settings(WartRemoverSettings.wartRemoverError)
  .settings(WartRemoverSettings.wartRemoverWarning)
  .settings(wartremover.WartRemover.autoImport.wartremoverErrors in(Test, compile) --= Seq(Wart.Any, Wart.Equals, Wart.Null, Wart.NonUnitStatements, Wart.PublicInference))
  .settings(wartremover.WartRemover.autoImport.wartremoverExcluded ++=
    routes.in(Compile).value ++
      (baseDirectory.value / "test").get ++
      Seq(sourceManaged.value / "main" / "sbt-buildinfo" / "BuildInfo.scala"))
  .settings(publishingSettings: _*)
  .settings(PlayKeys.playDefaultPort := 9212)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(integrationTestSettings())
  .configs(IntegrationTest)
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
      "-Yno-adapted-args",
      "-Ywarn-value-discard",
      "-Ywarn-dead-code",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-language:reflectiveCalls",
      "-Ypartial-unification", //required by cats
      "-Ywarn-unused:-imports"
    )
  )
