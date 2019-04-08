package com.dwolla.sbt.dwollabase

import sbt.Keys.scalacOptions
import sbt._
import sbt.internal._

object ToggleScalacOption {
  def apply(commandName: String, flag: String): Command =
    Command.command(commandName) { state: State =>
      val extracted = Project.extract(state)

      val adjust: Seq[String] ⇒ Seq[String] = Project.runTask(scalacOptions in Compile, state) match {
        case Some((_, Value(options))) if options.contains(flag) ⇒ _.filterNot(_ == flag)
        case _ ⇒ flag +: _
      }

      val updateSettings =
        Project.definitions(extracted.structure, true, scalacOptions.key)(Show.fromToString)
          .map(_ / scalacOptions)
          .flatMap { task =>
            Project.runTask(task, state)
              .collect {
                case (_, Value(options)) ⇒
                  task := adjust(options)
              }
          }

      SessionSettings.reapply(Project.setAll(extracted, updateSettings), state)
    }
}
