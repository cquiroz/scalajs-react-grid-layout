val reactJS         = "16.5.1"
val scalaJsReact    = "1.3.1"
val reactGridLayout = "0.16.6"
val scalaJSDom      = "0.9.6"

parallelExecution in (ThisBuild, Test) := false

cancelable in Global := true

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS::stopWebpackDevServer; demo/fastOptJS::startWebpackDevServer")

val root =
  project
    .in(file("."))
    .settings(commonSettings: _*)
    .aggregate(facade, demo)
    .settings(
      name := "root",
      // No, SBT, we don't want any artifacts for root.
      // No, not even an empty jar.
      publish := {},
      publishLocal := {},
      publishArtifact := false,
      Keys.`package` := file("")
    )

lazy val demo =
  project
    .in(file("demo"))
    .enablePlugins(GitVersioning)
    .enablePlugins(GitBranchPrompt)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(
      version in webpack := "4.28.2",
      version in startWebpackDevServer := "3.1.14",
      webpackConfigFile in fastOptJS := Some(
        baseDirectory.value / "src" / "webpack" / "webpack-dev.config.js"),
      webpackConfigFile in fullOptJS := Some(
        baseDirectory.value / "src" / "webpack" / "webpack-prod.config.js"),
      webpackMonitoredDirectories += (resourceDirectory in Compile).value,
      webpackResources := (baseDirectory.value / "src" / "webpack") * "*.js",
      includeFilter in webpackMonitoredFiles := "*",
      useYarn := true,
      webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
      webpackBundlingMode in fullOptJS := BundlingMode.Application,
      test := {},
      emitSourceMaps := false,
      webpackDevServerPort := 6060,
      npmDevDependencies in Compile ++= Seq(
        "css-loader"                    -> "0.28.11",
        "less"                          -> "2.3.1",
        "less-loader"                   -> "4.1.0",
        "mini-css-extract-plugin"       -> "0.4.0",
        "html-webpack-plugin"           -> "3.2.0",
        "url-loader"                    -> "1.0.1",
        "style-loader"                  -> "0.21.0",
        "postcss-loader"                -> "2.1.5",
        "cssnano"                       -> "3.10.0",
        "optimize-css-assets-webpack-plugin"                       -> "4.0.1",
        "webpack-merge"                 -> "4.1.0",
        "webpack-dev-server-status-bar" -> "1.0.0"
      ),
      npmDependencies in Compile ++= Seq(
        "react"           -> reactJS,
        "react-dom"       -> reactJS,
        "react-grid-layout" -> reactGridLayout
      ),
      // don't publish the demo
      publish := {},
      publishLocal := {},
      publishArtifact := false,
      Keys.`package` := file("")
    )
    .dependsOn(facade)

lazy val facade =
  project
    .in(file("facade"))
    .enablePlugins(GitVersioning)
    .enablePlugins(GitBranchPrompt)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "sjs-rgl",
      version in webpack := "4.28.2",
      version in startWebpackDevServer := "3.1.14",
      // Requires the DOM for tests
      requireJsDomEnv in Test          := true,
      // Compile tests to JS using fast-optimisation
      // scalaJSStage in Test            := FastOptStage,
      npmDependencies in Compile ++= Seq(
        "react"           -> reactJS,
        "react-dom"       -> reactJS,
        "react-grid-layout" -> reactGridLayout
      ),
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "core"        % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "extra"       % scalaJsReact,
        "org.scala-js"                      %%% "scalajs-dom" % scalaJSDom,
        "com.github.japgolly.scalajs-react" %%% "test"        % scalaJsReact % Test,
        "com.lihaoyi"                       %%% "utest"       % "0.6.6" % Test,
        "org.typelevel"                     %%% "cats-core"   % "1.5.0" % Test
      ),
      webpackConfigFile in Test       := Some(baseDirectory.value / "src" / "webpack" / "test.webpack.config.js"),
      testFrameworks += new TestFramework("utest.runner.Framework")
    )

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "io.github.cquiroz",
  description := "scala.js facade for react-grid-layout ",
  homepage := Some(url("https://github.com/cquiroz/sjs-rgl")),
  licenses := Seq(
    "BSD 3-Clause License" -> url(
      "https://opensource.org/licenses/BSD-3-Clause")),
  useGpg := true,
  publishArtifact in Test := false,
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots".at(nexus + "content/repositories/snapshots"))
    else
      Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
  },
  pomExtra := pomData,
  pomIncludeRepository := { _ =>
    false
  },
  scalacOptions := Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xfuture", // Turn on future language features.
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
    "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
    "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:option-implicit", // Option.apply used implicit view.
    "-Xlint:package-object-classes", // Class or object defined in package object.
    "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match", // Pattern match may not be typesafe.
    "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification", // Enable partial unification in type constructor inference
    "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",  // Warn if a local definition is unused.
    // "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
    "-Yrangepos",
    "-P:scalajs:sjsDefinedByDefault"
  ),
  // Settings to use git to define the version of the project
  git.useGitDescribe := true,
  git.formattedShaVersion := git.gitHeadCommit.value.map { sha =>
    s"v$sha"
  },
  git.uncommittedSignifier in ThisBuild := Some("UNCOMMITTED"),
  useGpg := true
)

lazy val pomData =
  <developers>
    <developer>
      <id>cquiroz</id>
      <name>Carlos Quiroz</name>
      <url>https://github.com/cquiroz</url>
      <roles>
        <role>Project Lead</role>
      </roles>
    </developer>
  </developers>
