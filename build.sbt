enablePlugins(ScalaJSPlugin)

name := "JSON serialization"
organization := "org.awesome"
version := "1.0"
scalaVersion := "2.13.1"
isSnapshot := true

scalaJSUseMainModuleInitializer := true
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.0-M2" % "test"
