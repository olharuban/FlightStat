name := "flights-statistic"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.commons" %  "commons-csv"           % "1.1",
  "joda-time"          %  "joda-time"             % "2.9.3",
  "org.joda"           %  "joda-convert"          % "1.8.1",
  "org.specs2"         %% "specs2-core"           % "3.3.1"   % Test,
  "org.specs2"         %% "specs2-mock"           % "3.3.1"   % Test,
  "org.specs2"         %% "specs2-matcher-extra"  % "3.3.1"   % Test,
  "org.mockito"        %  "mockito-all"           % "1.10.19" % Test
)
