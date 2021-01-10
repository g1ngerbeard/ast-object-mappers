name := "text-format-parsing"

version := "0.1"

scalaVersion := "2.13.4"

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-language:experimental.macros"
)

val versions = new {
  val magnolia = "0.17.0"
  val scalaTest = "3.2.2"
  val cats = "2.3.0"
  val fastparse = "2.2.2"
}

scalacOptions ++= Seq(
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % versions.fastparse,
  "com.propensive" %% "magnolia" % versions.magnolia,
  "org.typelevel" %% "cats-core" % versions.cats,
  "org.scalactic" %% "scalactic" % versions.scalaTest,
  "org.scalatest" %% "scalatest" % versions.scalaTest % Test
)
