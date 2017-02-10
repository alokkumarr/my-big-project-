import sbt.Keys._

name := """BDA-Transport-Service"""

version := "1.0"
resolvers += "MapR Maven Repository" at "http://repository.mapr.com/maven"
resolvers += "MapR Nexus Repository" at "http://repository.mapr.com/nexus"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories"
resolvers += Resolver.bintrayRepo("scalaz", "releases")
resolvers += Resolver.bintrayRepo("megamsys", "scala")

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(PlayNettyServer)
  .enablePlugins(RoutesCompiler)

scalaVersion := "2.11.8"
exportJars := true

unmanagedBase := baseDirectory.value / "lib"
unmanagedJars := Seq.empty[sbt.Attributed[java.io.File]]

//Generic Java modules and libraries
libraryDependencies ++= Seq(


  "com.mapr" % "mapr-release" % "5.1.0.37689-mapr",
  "org.reactivemongo" %% "reactivemongo-play-json" % "0.11.11",
  "ch.qos.logback" % "logback-classic" % "1.0.13"

)

// JUnit framework
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test

// ScalaTest framework
libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test
libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.3.0"

libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.1.1-mapr-1602"   
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.1.1-mapr-1602"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.0-mapr-1607"

libraryDependencies += "org.asynchttpclient" % "async-http-client" % "2.0.0"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2"
libraryDependencies += "org.apache.httpcomponents" % "httpasyncclient" % "4.1.1"
libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.4.5"
libraryDependencies += "commons-httpclient" % "commons-httpclient" % "3.1"

libraryDependencies += "com.typesafe.play" %% "filters-helpers" % "2.5.10"



// https://mvnrepository.com/artifact/com.typesafe.play/routes-compiler_2.10
libraryDependencies += "com.typesafe.play" % "routes-compiler_2.11" % "2.5.4"

val json4sNative = "org.json4s" %% "json4s-native" % "3.3.0"
val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.3.0"


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true
fork in test := true


// Dynamic configuration parameters:
//PlayKeys.devSettings := Seq("play.server.http.port" -> "8080")
// Run parameters: run -Dhttp.port=1234
