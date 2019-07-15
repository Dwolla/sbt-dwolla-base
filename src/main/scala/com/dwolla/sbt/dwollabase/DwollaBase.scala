package com.dwolla.sbt.dwollabase

import sbt._
import _root_.io.github.davidgregory084.TpolecatPlugin
import sbt.Keys._

object DwollaBase extends AutoPlugin {

  override def requires = TpolecatPlugin

  override def trigger  = allRequirements

  override def buildSettings = Seq(
    scalaVersion := {
      if ((baseDirectory.value / ".travis.yml").exists()) (crossScalaVersions in Global).value.last else "2.12.8"
    },
  )

  /** See https://github.com/tpolecat/tpolecat.github.io/issues/7 and https://github.com/scala/bug/issues/11175
    */
  private def replaceWarnUnusedParams(options: Seq[String],
                                      scalaBinVersion: String): Seq[String] =
    options.filterNot(_ == "-Ywarn-unused:params") ++ Option("-Ywarn-unused:explicits").filter(_ => scalaBinVersion == "2.12")

  override def projectSettings = Seq(
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
    resolvers ++= Seq(
      Resolver.bintrayRepo("dwolla", "maven"),
      Resolver.sonatypeRepo("releases"),
    ),
    scalacOptions := replaceWarnUnusedParams(scalacOptions.value, scalaBinaryVersion.value),
    scalacOptions --= sys.props.get("idea.runid").map(_ => "-Xfatal-warnings"),
    commands ++= Seq(
      ToggleScalacOption("toggleFatalWarnings", "-Xfatal-warnings"),
      ToggleScalacOption("toggleImplicitLogging", "-Xlog-implicits"),
    ),
  )
}
