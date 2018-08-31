name := "scala 2.12 scalac options"

val app = project in file(".")

TaskKey[Unit]("check") := {
  val scalaVersionValue = scalaVersion.value
  val scalacOptionsValue = scalacOptions.value
  val expected = Seq(
    "-deprecation",
    "-encoding",
    "-explaintypes",
    "-feature",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Xfuture",
    "-Xlint:adapted-args",
    "-Xlint:by-name-right-associative",
    "-Xlint:delayedinit-select",
    "-Xlint:doc-detached",
    "-Xlint:inaccessible",
    "-Xlint:infer-any",
    "-Xlint:missing-interpolator",
    "-Xlint:nullary-override",
    "-Xlint:nullary-unit",
    "-Xlint:option-implicit",
    "-Xlint:package-object-classes",
    "-Xlint:poly-implicit-overload",
    "-Xlint:private-shadow",
    "-Xlint:stars-align",
    "-Xlint:type-parameter-shadow",
    "-Xlint:unsound-match",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-extra-implicit",
    "-Xlint:constant",
    "-Ywarn-unused:explicits",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
  )
  val scalaVersionError = scalaVersionValue match {
    case "2.12.6" ⇒ None
    case _ ⇒ Option(
      s"""scalaVersion does not contain the expected value.
         |
         |  Expected: 2.12.6
         |
         |  Found:    $scalaVersionValue
         |
      """.stripMargin
    )
  }

  val missingItems = expected.flatMap { e ⇒
    if (scalacOptionsValue.contains(e)) List.empty
    else List(e)
  }

  val encodingIssue: List[String] = scalacOptionsValue.sliding(2).collect {
    case "-encoding" :: encoding :: Nil if encoding != "utf-8" ⇒ s"""Invalid encoding "$encoding""""
    case "-encoding" :: Nil ⇒ "missing encoding"
  }.toList

  val scalacOptionsError = if (missingItems.isEmpty && encodingIssue.isEmpty) None
  else {
    Option(
      s"""scalacOptions does not contain the expected values.
        |
        |  Missing Options:     $missingItems
        |
        |  Encoding Issues:     $encodingIssue
        |
      """.stripMargin)
  }

  val allErrors: Seq[String] = Seq(scalaVersionError, scalacOptionsError).filter {
    case Some(_) ⇒ true
    case None ⇒ false
  }.map {
    case Some(msg) ⇒ s"Error: $msg"
  }
  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
