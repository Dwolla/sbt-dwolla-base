{
  val pluginVersion = System.getProperty("plugin.version")
  if(pluginVersion == null)
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                 |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  else addSbtPlugin("com.dwolla.sbt" % "sbt-dwolla-base" % pluginVersion)
}

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
addSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.2.0")
