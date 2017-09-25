import com.trueaccord.scalapb.compiler.Version.scalapbVersion

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.1")

name := "scalapb-json4s"

organization := "com.scalableminds"

organizationName := "scalable minds UG (haftungsbeschränkt) & Co. KG"

organizationHomepage := Some(url("http://scalableminds.com"))

startYear := Some(2017)

description := "A small library to load webknossos-wrap encoded files."

homepage := Some(url("https://github.com/scalableminds/webknossos-wrap"))

scmInfo := Some(ScmInfo(
  url("https://github.com/scalableminds/webknossos-wrap"),
  "https://github.com/scalableminds/webknossos-wrap.git"))

scalacOptions in ThisBuild ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 11 => List("-target:jvm-1.7")
    case _ => Nil
  }
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo <<= version { (version: String) =>
  val rootDir = "/srv/maven/"
  val path =
    if (version.trim.endsWith("SNAPSHOT"))
      "snapshots"
    else
      "releases"
  Some("scm.io intern repo" at "s3://maven.scm.io.s3-eu-central-1.amazonaws.com/" + path)
}

libraryDependencies ++= Seq(
  "com.trueaccord.scalapb" %% "scalapb-runtime" % scalapbVersion,
  "org.json4s" %% "json4s-jackson" % "3.5.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.google.protobuf" % "protobuf-java-util" % "3.2.0" % "test",
  "com.google.protobuf" % "protobuf-java" % "3.2.0" % "protobuf"
)

lazy val Proto26Test = config("proto26") extend(Test)

lazy val root = (project in file("."))
  .configs(Proto26Test)
  .settings(
    inConfig(Proto26Test)(
      Defaults.testSettings ++
      sbtprotoc.ProtocPlugin.protobufConfigSettings
    ),
    inConfig(Test)(sbtprotoc.ProtocPlugin.protobufConfigSettings)
  )

PB.protocVersion in Proto26Test := "-v261"

PB.protoSources in Proto26Test := Seq((sourceDirectory in Proto26Test).value / "protobuf")

PB.targets in Compile := Nil

PB.targets in Test := Seq(
  PB.gens.java -> (sourceManaged in Test).value,
  scalapb.gen(javaConversions=true) -> (sourceManaged in Test).value
)

PB.targets in Proto26Test := Seq(
  scalapb.gen() -> (sourceManaged in Proto26Test).value
)
