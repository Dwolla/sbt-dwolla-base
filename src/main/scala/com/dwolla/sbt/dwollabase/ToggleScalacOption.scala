package com.dwolla.sbt.dwollabase

import sbt.Keys.scalacOptions
import sbt._

object ToggleScalacOption {
  def apply(commandName: String, flag: String, friendlyName: String): Command =
    Command.command(commandName) { state =>
      val disableImplicitLogging = s"""set scalacOptions -= "$flag""""
      val enableImplicitLogging = s"""set scalacOptions += "$flag""""

      Project.runTask(scalacOptions in Compile, state) match {
        case None => state
        case Some((_, Inc(_))) => state
        case Some((s0, Value(options))) if options.contains(flag) =>
          val s1 = Command.process(disableImplicitLogging, s0)
          state.log.info(s"$friendlyName disabled")
          s1

        case Some((s0, Value(options))) if !options.contains(flag) =>
          val s1 = Command.process(enableImplicitLogging, s0)
          state.log.info(s"$friendlyName enabled")
          s1
      }
    }

}
