name := "stats"

version := "1.0"

scalaVersion  := "2.11.6"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

libraryDependencies ++= {
  val akkaV = "2.4.2"
  val sprayV = "1.3.3"
  val json4sV = "3.3.0"
  val reactiveMongoV = "0.11.10"
  val scalaTestV = "2.2.6"
  val sprayJsonV = "1.3.2"
  val scalaTestEmbedmongoV = "0.2.3-SNAPSHOT"
  val nscalaTimeV = "2.10.0";
  
  Seq(
    "org.reactivemongo" %% "reactivemongo" % reactiveMongoV,
    "io.spray"            %%   "spray-can"     % sprayV,
    "io.spray"            %%   "spray-routing" % sprayV,
    "io.spray"            %%   "spray-http"    % sprayV,
    "io.spray"            %%   "spray-util"    % sprayV,
    "io.spray"            %%   "spray-httpx"   % sprayV,
    "io.spray"            %%  "spray-json"     % sprayJsonV,
    "io.spray"            %%  "spray-client"   % sprayV,
    "io.spray"            %%   "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"     % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"   % akkaV   % "test",
    "org.scalatest"       %%  "scalatest"      % scalaTestV % "test",
    "com.github.nscala-time" %% "nscala-time"  % nscalaTimeV,
    "org.json4s"          %% "json4s-native"   % json4sV,
    "org.json4s"          %% "json4s-jackson"  % json4sV,
    "org.json4s"          %% "json4s-ext"      % json4sV,
    "com.github.simplyscala" %% "scalatest-embedmongo" % scalaTestEmbedmongoV
  )
}

Defaults.itSettings


resolvers ++= Seq("spray" at "http://repo.spray.io/")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers ++= Seq("Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")

resolvers ++= Seq("Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/")


coverageHighlighting := false
parallelExecution in Test := false
Revolver.settings
