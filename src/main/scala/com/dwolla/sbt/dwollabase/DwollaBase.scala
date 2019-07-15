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

  private def addCompilerPluginBeforeVersion13(dependency: ModuleID): Setting[Seq[ModuleID]] =
    libraryDependencies ++= CrossVersion.partialVersion(scalaVersion.value).collect {
      case (2, x) if x < 13 => compilerPlugin(dependency)
    }

  override def projectSettings = Seq(
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.10.3" cross CrossVersion.binary),
    addCompilerPluginBeforeVersion13("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),
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
