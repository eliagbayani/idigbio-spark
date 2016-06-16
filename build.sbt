name := "iDigBio-LD"

version := "1.5.2"

scalaVersion := "2.10.5"

val sparkV: String = "1.6.1"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "16.0.1",
  "net.sf.opencsv" % "opencsv" % "2.3",
  "org.apache.commons" % "commons-csv" % "1.1",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "com.databricks" % "spark-csv_2.10" % "1.3.0",
  "io.spray" %%  "spray-json" % "1.3.2",
  "org.globalnames" %% "gnparser" % "0.3.0" excludeAll(
    ExclusionRule(organization = "org.scala-lang")),
  "org.locationtech.spatial4j" % "spatial4j" % "0.6",
  "com.vividsolutions" % "jts-core" % "1.14.0",
  "org.apache.spark" %% "spark-sql" % sparkV % "provided",
  "org.apache.spark" %% "spark-streaming-kafka" % sparkV excludeAll(
    ExclusionRule("org.spark-project.spark", "unused")
    ),
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.0" excludeAll(
    ExclusionRule(organization = "io.netty"),
    ExclusionRule("org.slf4j", "slf4j-api"),
    ExclusionRule("com.google.guava", "guava")),

  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "com.holdenkarau" %% "spark-testing-base" % "1.6.1_0.3.3" % "test"
)

test in assembly := {}

javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

resolvers += Resolver.sonatypeRepo("public")
