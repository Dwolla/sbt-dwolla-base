name := "scala 2.13 scalac options"

ThisBuild / scalaVersion := "2.13.0"

val subproject = project in file("subproject")

val app = (project in file(".")).aggregate(subproject)

TaskKey[Unit]("check") := {
  val scalaVersions = Seq("root" -> scalaVersion.value, "app" -> (app / scalaVersion).value, "subproject" -> (subproject / scalaVersion).value)
  val scalaVersionErrors = scalaVersions.flatMap {
    case (_, "2.13.0") ⇒ None
    case (k, v) ⇒ Option(
      s"""`$k / scalaVersion` does not contain the expected value.
         |
         |  Expected: 2.13.0
         |
         |  Found:    $v
         |
         |""".stripMargin
    )
  }

  val allErrors: Seq[String] =
    (scalaVersionErrors)
      .map(msg ⇒ s"Error: $msg")

  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
