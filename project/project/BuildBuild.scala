import sbt._

object BuildBuild extends Build {

  override lazy val settings = super.settings ++ sbtPlugins

  def sbtPlugins = Seq(
    "com.timushev.sbt" % "sbt-updates" % "0.1.6",
    "com.github.malliina" %% "sbt-utils" % "0.0.5",
    "com.github.malliina" %% "ssh-client" % "0.0.3",
    "com.eed3si9n" % "sbt-assembly" % "0.11.2"
  ) map (_.withSources()) map addSbtPlugin

  override lazy val projects = Seq(root)
  lazy val root = Project("plugins", file("."))
}