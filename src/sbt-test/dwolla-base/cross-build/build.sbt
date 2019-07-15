name := "cross build options"

val app = project in file(".")

TaskKey[Unit]("check212") := {
  val scalaBinaryValue = scalaBinaryVersion.value
  val scalacOptionsValue = scalacOptions.value

  val scalaBinaryValueError = scalaBinaryValue match {
    case "2.12" ⇒ None
    case _ ⇒ Option(s"Expected Scala version 2.12, got $scalaBinaryValue")
  }

  val scalacOptionsTest = if (scalacOptionsValue.contains("-Ywarn-extra-implicit")) None
  else Option(s"Scala $scalaBinaryValue scalacOptions did not contain `-Ywarn-extra-implicit`, but it should")

  val allErrors = List(scalaBinaryValueError, scalacOptionsTest).flatMap(_.toList)
  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}

TaskKey[Unit]("check211") := {
  val scalaBinaryValue = scalaBinaryVersion.value
  val scalacOptionsValue = scalacOptions.value

  val scalaBinaryValueError = scalaBinaryValue match {
    case "2.11" ⇒ None
    case _ ⇒ Option(s"Expected Scala version 2.11, got $scalaBinaryValue")
  }

  val scalacOptionsTest = if (!scalacOptionsValue.contains("-Ywarn-extra-implicit")) None
  else Option(s"Scala $scalaBinaryValue scalacOptions contains `-Ywarn-extra-implicit`, but it shouldn't")

  val allErrors = List(scalaBinaryValueError, scalacOptionsTest).flatMap(_.toList)
  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}

TaskKey[Boolean]("crossBuild") := {
  val targetDir = target.value
  val scalaBinaryValue = scalaBinaryVersion.value

  val output = targetDir / scalaBinaryValue

  output.createNewFile()
}

TaskKey[Boolean]("writeScalaVersion") := {
  val output = target.value / scalaVersion.value

  output.createNewFile()
}
