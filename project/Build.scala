import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "site2"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "org.mongodb" %% "casbah" % "2.5.0"//,
//    "com.novus" %% "salat" % "1.9.2-SNAPSHOT"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
