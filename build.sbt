name := "spark-word-count"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "com.holdenkarau" %% "spark-testing-base" % "2.3.0_0.9.0" % "test"
)

enablePlugins(SonarRunnerPlugin)

fork in Test := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")