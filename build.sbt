enablePlugins(PackPlugin)

name := "simple-predictor"

version := "0.1"

scalaVersion := "2.12.7"

unmanagedBase := baseDirectory.value / "dependencies"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.6.1",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.scalaj" %% "scalaj-http" % "2.4.1" % Test
)

packMain := Map("simple-predictor" -> "simple.predictor.Run")

packGenerateWindowsBatFile := false

packJvmOpts := Map("simple-predictor" -> Seq("-Xms3g", "-Xmx5g"))