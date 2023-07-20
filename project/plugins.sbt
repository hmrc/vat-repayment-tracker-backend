import sbt.{MavenRepository, Resolver}

resolvers += MavenRepository("HMRC-open-artefacts-maven", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc"       % "sbt-auto-build"         % "3.9.0")
//addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.2.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-artifactory"        % "2.0.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-distributables"     % "2.1.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"             % "2.8.8")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"          % "1.8.2")
addSbtPlugin("org.wartremover"   % "sbt-wartremover"        % "3.0.7")
addSbtPlugin("org.scalariform"   % "sbt-scalariform"        % "1.8.3")
addSbtPlugin("org.scalastyle"    %% "scalastyle-sbt-plugin" % "1.0.0")
// Provides useful 'dependencyTree' and 'whatDependsOn' tasks for dependency management
addSbtPlugin("net.virtual-void"  % "sbt-dependency-graph"   % "0.10.0-RC1")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"            % "0.6.3")