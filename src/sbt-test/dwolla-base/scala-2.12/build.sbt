name := "scala 2.12 scalac options"

val subproject = project in file("subproject")

val app = (project in file(".")).aggregate(subproject)

TaskKey[Unit]("check") := {
  val scalaVersions = Seq("root" -> scalaVersion.value, "app" -> (app / scalaVersion).value, "subproject" -> (subproject / scalaVersion).value)
  val scalacOptionsValue = Seq("root" -> scalacOptions.value, "app" -> (app / scalacOptions).value, "subproject" -> (subproject / scalacOptions).value)
  val resolversValue = Seq("root" -> resolvers.value, "app" -> (app / resolvers).value, "subproject" -> (subproject / resolvers).value)

  val expectedScalacOptions = Seq(
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
  val scalaVersionErrors = scalaVersions.flatMap {
    case (_, "2.12.8") ⇒ None
    case (k, v) ⇒ Option(
      s"""`$k / scalaVersion` does not contain the expected value.
         |
         |  Expected: 2.12.8
         |
         |  Found:    $v
         |
         |""".stripMargin
    )
  }

  val expectedResolver = Resolver.bintrayRepo("dwolla", "maven")
  val resolversError = resolversValue.flatMap {
    case (_, v) if v.contains(expectedResolver) => None
    case (k, v) => Option(
      s"""`$k / resolvers` does not contain the expected values.
         |
         |  Expected: $expectedResolver
         |
         |  Found:    $v
         |
       """.stripMargin)
  }

  val scalacOptionsError = scalacOptionsValue.flatMap { case (k, v) =>
    val missingItems = expectedScalacOptions.flatMap { e ⇒
      if (v.contains(e)) List.empty
      else List(e)
    }

    val encodingIssue: List[String] = v.sliding(2).collect {
      case "-encoding" :: encoding :: Nil if encoding != "utf-8" ⇒ s"""Invalid encoding "$encoding""""
      case "-encoding" :: Nil ⇒ "missing encoding"
    }.toList

    if (missingItems.isEmpty && encodingIssue.isEmpty) None
    else {
      Option(
        s"""`$k / scalacOptions` does not contain the expected values.
           |
           |  Missing Options:     $missingItems
           |
           |  Encoding Issues:     $encodingIssue
           |""".stripMargin)
    }
  }

  val allErrors: Seq[String] =
    (scalaVersionErrors ++ resolversError ++ scalacOptionsError)
      .map(msg ⇒ s"Error: $msg")

  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
