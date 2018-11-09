name := "Toggling -Xfatal-warnings on and off"

val app = project in file(".")

TaskKey[Unit]("fatalWarningsIsOn") := {
  val scalacOptionsValue = scalacOptions.value

  val scalacOptionsError = if (scalacOptionsValue.contains("-Xfatal-warnings")) None
  else Option(s"""scalacOptions does not contain "-Xfatal-warnings".""")

  val allErrors: Seq[String] = Seq(scalacOptionsError)
    .collect {
      case Some(msg) ⇒ s"Error: $msg"
    }

  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}

TaskKey[Unit]("fatalWarningsIsOff") := {
  val scalacOptionsValue = scalacOptions.value

  val scalacOptionsError = if (!scalacOptionsValue.contains("-Xfatal-warnings")) None
  else Option(s"""scalacOptions contains "-Xfatal-warnings".""")

  val allErrors: Seq[String] = Seq(scalacOptionsError)
    .collect {
      case Some(msg) ⇒ s"Error: $msg"
    }

  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
