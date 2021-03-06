lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "sbt-dwolla-base",
  homepage := Some(url("https://github.com/Dwolla/sbt-dwolla-base")),
  description := "Dwolla base Scala settings",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  sbtPlugin := true,
  startYear := Option(2018),
  addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.7"),
)

lazy val releaseSettings = {
  import ReleaseTransformations._
  import sbtrelease.Version.Bump._
  Seq(
    releaseVersionBump := Minor,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      releaseStepCommandAndRemaining("^ test"),
      releaseStepCommandAndRemaining("^ scripted"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("^ publish"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
}

lazy val bintraySettings = Seq(
  bintrayVcsUrl := homepage.value.map(_.toString),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

lazy val `sbt-dwolla-base` = (project in file("."))
  .settings(buildSettings ++ bintraySettings ++ releaseSettings: _*)
  .enablePlugins(SbtPlugin)
