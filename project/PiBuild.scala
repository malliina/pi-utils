import com.mle.sbtutils.SbtProjects
import com.mle.ssh.{RemoteConfigReader, RootRemoteInfo, SSH}
import com.mle.util.Utils
import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._

import scala.concurrent.duration.DurationLong

/**
 * A scala build file template.
 */
object PiBuild extends Build {

  val remoteRun = taskKey[Unit]("Builds a local jar, transfers it to a remote machine and runs it remotely")
  val conf = taskKey[RootRemoteInfo]("The config")
  lazy val piProject = SbtProjects.testableProject("pi-utils").settings(projectSettings: _*)

  lazy val projectSettings = assemblySettings ++ remoteSettings ++ Seq(
    version := "0.1.1",
    scalaVersion := "2.11.2",
    organization := "com.github.malliina",
    fork in Test := true,
    test in assembly := {},
    mainClass in assembly := Some("com.mle.pi.Hello"),
    libraryDependencies ++= Seq(
      "com.pi4j" % "pi4j-core" % "0.0.5",
      "io.reactivex" %% "rxscala" % "0.21.1",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.1", // scalatest 2.11 requires
      "com.github.malliina" %% "util-base" % "0.3.0") map (_.withSources())
  )

  def remoteSettings = Seq(
    conf := RemoteConfigReader.load,
    remoteRun := {
      val log = streams.value.log
      val file = assembly.value.toPath
      val remoteFileName = file.getFileName.toString
      val conf = RemoteConfigReader.load
      Utils.using(new SSH(conf.host, conf.port, conf.user, conf.key))(ssh => {
        log.info(s"Transferring: ${file.toAbsolutePath} to: $remoteFileName at: ${conf.user}@${conf.host}:${conf.port}...")
        ssh.scpAwait(file, remoteFileName)
        val runJarCommand = s"java -jar /home/${conf.user}/$remoteFileName"
        log.info(s"Transfer done. Running: $runJarCommand as root...")
        val response = ssh.execute("sudo -s -S", conf.rootPassword, runJarCommand, "exit")
        val sub = response.output.subscribe(line => log.info(line))
        val result = response.await(240.seconds)
        log.info(s"Exit: ${result.exitValue}")
        sub.unsubscribe()
      })
    }
  )
}