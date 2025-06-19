import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.prac.la"
ThisBuild / version := "0.0.5-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/prac-la"),
    "scm:git@github.com:openurp/prac-la.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "The OpenURP Practice Lawyer Assisstant"
ThisBuild / homepage := Some(url("http://openurp.github.io/prac-la/index.html"))
ThisBuild / resolvers += Resolver.mavenLocal

val apiVer = "0.44.0"
val starterVer = "0.3.58"
val baseVer = "0.4.51"
val openurp_base_api = "org.openurp.base" % "openurp-base-api" % apiVer
val openurp_degree_api = "org.openurp.degree" % "openurp-degree-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, UndertowPlugin, TomcatPlugin)
  .settings(
    name := "openurp-prac-la-webapp",
    common,
    libraryDependencies ++= Seq(beangle_ems_app,beangle_webmvc),
    libraryDependencies ++= Seq(openurp_base_api, openurp_degree_api, openurp_stater_web),
    libraryDependencies ++= Seq(beangle_doc_docx,openurp_base_tag),
    libraryDependencies ++= Seq(logback_classic)
  )

