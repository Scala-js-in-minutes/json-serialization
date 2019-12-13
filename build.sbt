enablePlugins(ScalaJSPlugin)

name := "JSON serialization"
organization := "org.awesome"
version := "1.0"
scalaVersion := "2.13.1"
isSnapshot := true

scalaJSUseMainModuleInitializer := true
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
