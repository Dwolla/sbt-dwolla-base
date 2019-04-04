name := "Toggling -Xfatal-warnings on and off"

val subproject = project in file("subproject")

val app = (project in file("."))
    .aggregate(subproject)

TaskKey[Unit]("fatalWarningsIsOn") := {
  val scalacOptionsValue = scalacOptions.value
  val subprojectScalacOptionsValue = (scalacOptions in subproject).value

  val scalacOptionsError = Seq(scalacOptionsValue → "root", subprojectScalacOptionsValue → "subproject").map { case (v, k) ⇒
    if (v.contains("-Xfatal-warnings")) None
    else Option(s"""$k scalacOptions does not contain "-Xfatal-warnings", but it should.""")
  }

  val allErrors: Seq[String] = scalacOptionsError
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
  val subprojectScalacOptionsValue = (scalacOptions in subproject).value

  val scalacOptionsError = Seq(scalacOptionsValue → "root", subprojectScalacOptionsValue → "subproject").map { case (v, k) ⇒
    if (!v.contains("-Xfatal-warnings")) None
    else Option(s"""$k scalacOptions contains "-Xfatal-warnings", but it should not.""")
  }

  val allErrors: Seq[String] = scalacOptionsError
    .collect {
      case Some(msg) ⇒ s"Error: $msg"
    }

  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
