package com.dwolla.sbt.dwollabase

import sbt.Keys.scalacOptions
import sbt._

object ToggleFatalWarnings {
  val fatalWarningsFlag = "-Xfatal-warnings"

  val disableFatalWarnings = s"""set scalacOptions -= "$fatalWarningsFlag""""
  val enableFatalWarnings = s"""set scalacOptions += "$fatalWarningsFlag""""

  def apply(): Command = Command.command("toggleFatalWarnings") { state =>
    Project.runTask(scalacOptions in Compile, state) match {
      case None => state
      case Some((_, Inc(_))) => state
      case Some((newState, Value(options))) if options.contains(fatalWarningsFlag) =>
        Command.process(disableFatalWarnings, newState)
      case Some((newState, Value(options))) if !options.contains(fatalWarningsFlag) =>
        Command.process(enableFatalWarnings, newState)
    }
  }
}
