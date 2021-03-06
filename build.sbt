import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import LDMLTasks._

val cldrVersion = settingKey[String]("The version of CLDR used.")
lazy val downloadFromZip: TaskKey[Unit] =
  taskKey[Unit]("Download the sbt zip and extract it")

val commonSettings: Seq[Setting[_]] = Seq(
  cldrVersion := "29",
  version := s"0.1.0-SNAPSHOT+${cldrVersion.value}",
  organization := "com.github.cquiroz",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.4", "2.11.8"),
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),
  mappings in (Compile, packageBin) ~= {
    // Exclude CLDR files...
    _.filter(!_._2.contains("core"))
  },
  exportJars := true,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra :=
      <developers>
        <developer>
          <id>cquiroz</id>
          <name>Carlos Quiroz</name>
          <url>https://github.com/cquiroz/</url>
        </developer>
      </developers>
  ,
  pomIncludeRepository := { _ => false }
)

lazy val scalajs_locales: Project = project.in(file("."))
  .settings(commonSettings)
  .settings(
    publish := {},
    publishLocal := {}
  )
  .aggregate(coreJS, coreJVM, testSuiteJS, testSuiteJVM)

lazy val core: CrossProject = crossProject.crossType(CrossType.Pure).
  settings(commonSettings: _*).
  settings(
    name := "scalajs-locales",
    downloadFromZip := {
      val xmlFiles = ((resourceDirectory in Compile) / "core").value
      if (java.nio.file.Files.notExists(xmlFiles.toPath)) {
        println(s"CLDR files missing, downloading version ${cldrVersion.value} ...")
        IO.unzipURL(
          new URL(s"http://unicode.org/Public/cldr/${cldrVersion.value}/core.zip"),
          xmlFiles)
      } else {
        println("CLDR files already available")
      }
    },
    compile in Compile <<= (compile in Compile).dependsOn(downloadFromZip),
    sourceGenerators in Compile += Def.task {
      generateLocaleData((sourceManaged in Compile).value,
        ((resourceDirectory in Compile) / "core").value)
    }.taskValue
  ).
  jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))

lazy val coreJS: Project = core.js
lazy val coreJVM: Project = core.jvm

lazy val testSuite: CrossProject = CrossProject(
  jvmId = "testSuiteJVM",
  jsId = "testSuite",
  base = file("testSuite"),
  crossType = CrossType.Full).
  jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin)).
  settings(commonSettings: _*).
  settings(
    publish := {},
    publishLocal := {},
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"),
        "-v", "-a")
  ).
  jsSettings(
    name := "scalajs-locales testSuite on JS",
    scalaJSUseRhino := false
  ).
  jsConfigure(_.dependsOn(coreJS)).
  jvmSettings(
    // Fork the JVM test to ensure that the custom flags are set
    fork in Test := true,
    // Use CLDR provider for locales
    // https://docs.oracle.com/javase/8/docs/technotes/guides/intl/enhancements.8.html#cldr
    javaOptions in Test ++= Seq("-Duser.language=en", "-Duser.country=", "-Djava.locale.providers=CLDR"),
    name := "scalajs-locales testSuite on JVM",
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test"
  ).
  jvmConfigure(_.dependsOn(coreJVM))

lazy val testSuiteJS: Project = testSuite.js
lazy val testSuiteJVM: Project = testSuite.jvm
