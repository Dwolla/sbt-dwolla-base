package com.dwolla.sbt.dwollabase

import sbt._
import _root_.io.github.davidgregory084.TpolecatPlugin
import sbt.Keys._

object DwollaBase extends AutoPlugin {

  override def requires = TpolecatPlugin

  override def trigger  = allRequirements

  override def buildSettings = Seq(
    scalaVersion := {
      Def.settingDyn {
        if (SettingKey[Boolean]("isTravisBuild", "Flag indicating whether the current build is running under Travis").?.value.isDefined)
          Def.settingDyn {
            Def.setting((crossScalaVersions in ThisBuild).value.last)
          }
        else
          Def.setting("2.12.11")
      }
    }.value,
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
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),
    addCompilerPluginBeforeVersion13("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
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
