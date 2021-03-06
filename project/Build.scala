import sbt._, Keys._
import com.github.bigtoast.sbtliquibase.LiquibasePlugin._
import com.earldouglas.xsbtwebplugin._
import WebPlugin.webSettings
import PluginKeys._

object BuildSettings {
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    scalacOptions += "-deprecation",
    crossScalaVersions := Seq("2.8.1", "2.9.0", "2.9.0-1", "2.9.1", "2.9.2"),
    resolvers ++= Seq(
      ScalaToolsReleases,
      "BeCompany Nexus" at "http://nexus.becompany.ch/nexus/content/groups/public/",
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
      "Shiro Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Shiro Snapshots" at "https://repository.apache.org/content/repositories/snapshots/",
      "sonatype.repo" at "https://oss.sonatype.org/content/repositories/public/"
    ),
/*
    publishTo <<= version { (v: String) => 
      val nexus = "https://oss.sonatype.org/" 
        if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
        else Some("releases" at nexus + "service/local/staging/deploy/maven2") 
    },
*/
    publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))),

    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),

    libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

  )
}

object ChamoisBuild extends Build {

  val liftVersion = "2.5"

  lazy val root = Project("chamois", file("."),
    settings = BuildSettings.buildSettings ++ Seq(
      // the root is just an aggregator so dont publish a JAR
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Test, packageBin) := false,
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in (Compile, packageSrc) := false,
      libraryDependencies ++= Seq(
        "org.squeryl" %% "squeryl" % "0.9.5-6",
        "org.apache.derby" % "derby" % "10.9.1.0",
        "net.liftweb" %% "lift-webkit" % liftVersion,
        //"net.liftmodules" %% "lift-openid" % liftVersion % "compile->default",
        "net.liftmodules" %% "openid" % "2.5-RC4-1.2" excludeAll(ExclusionRule(organization = "net.liftweb")),
        "net.liftweb" %% "lift-squeryl-record" % liftVersion,
        "net.devkat" % "dgrid-lift" % "1.0-SNAPSHOT",
        "ch.becompany" % "fugue-icons-lift" % "3.4.1",
        "eu.getintheloop" %% "lift-shiro" % "0.0.7-201306201324",
        "net.databinder.dispatch" %% "dispatch-core" % "0.9.4",
        "commons-collections" % "commons-collections" % "3.2.1",
        //"commons-beanutils" % "commons-beanutils" % "20030211.134440",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.11.v20130520" % "container,test",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,compile" artifacts Artifact("javax.servlet", "jar", "jar"),
        "postgresql" % "postgresql" % "9.1-901.jdbc4",
        "commons-logging" % "commons-logging" % "1.1.1",
        "ch.qos.logback" % "logback-classic" % "1.0.6",
        "org.apache.tika" % "tika-parsers" % "1.2" excludeAll(ExclusionRule(organization = "org.bouncycastle"), ExclusionRule(organization = "org.aspectj")),
        "junit" % "junit" % "4.10" % "test",
        "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"
      )
    ) ++
    net.virtualvoid.sbt.graph.Plugin.graphSettings ++
    webSettings ++ Seq(
      (webappResources in Compile) <+= (resourceManaged in Compile)(_ / "webapp")
    ) ++
    liquibaseSettings ++ Seq (
      liquibaseUrl := "jdbc:postgresql:chamois",
      liquibaseDriver := "org.postgresql.Driver",
      liquibaseUsername := "chamois",
      liquibasePassword := "chamois",
      liquibaseChangelog := "src/main/migrations/changelog.xml"
    )
  )
  
}
