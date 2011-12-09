import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "scalanews"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "com.ocpsoft" % "ocpsoft-pretty-time" % "1.0.7"
    )

    val main = PlayProject(appName, appVersion, appDependencies).settings(defaultScalaSettings:_*).settings(
      // Add your own project settings here      
    )

}
