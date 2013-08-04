[
 :module "Baumeister"
 :project "org.soulspace.build"
 :project-lead "Ludger Solbach"
 :description "Baumeister Build System"
 :type :consolefrontend
 :version "0.4.5"
 :author "Ludger Solbach"
 :provider "soulspace.org"
 :inception-year 2012
 :plugins ["global" "dependencies" "maven" "eclipse" "clojure" "clojuretest" "package" "release" "bmdebug"]
 ; :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :repository-root-dir "/home/soulman/devel/repositories" ; FIXME use $HOME here
 ; :extra-repositories [[:maven-proxy :thirdparty "http://maven.alfresco.com/nexus/content/groups/public" "${baumeister-home-dir}/../repositories/maven.alfresco.com"]]
 :dependencies [[["org.clojure" "clojure" "1.5.1"] :runtime]
                [["org.soulspace.clj" "CljArtifactLibrary" "0.4.3"]]
                [["org.soulspace.clj" "CljVersionLibrary" "0.4.3"]] ; transitive CljArtifactLibrary
                [["org.soulspace.clj" "CljJavaLibrary" "0.3.0"]]
                [["org.tcrawley" "dynapath" "0.2.3"]] ; dynamic classpath support
                [["org.soulspace.clj" "CljXmlLibrary" "0.2.0"]] ; maven support
                [["org.clojure" "data.zip" "0.1.1"]] ; maven support
                [["org.clojure" "tools.cli" "0.2.2"]] ; maven support
                [["org.apache.ant" "ant" "1.8.3"]]
                [["org.apache.ant" "ant-junit" "1.8.3"]] ; junit plugin
                [["org.apache.ant" "ant-jdepend" "1.8.3"]] ; jdepend plugin
                [["org.aspectj" "aspectjrt" "1.6.11"]] ; aspectj plugin
                [["net.sourceforge.cobertura" "cobertura" "1.9.4.1"]] ; cobertura plugin
                [["jdepend" "jdepend" "2.9.1"]] ; jdepend plugin
                [["net.sourceforge.pmd" "pmd" "5.0.0"]] ; pmd plugin
                ; add additional findbugs dependencies (findbugs, findbugs-ant, ...)
                [["com.google.code.findbugs" "findbugs-ant" "2.0.0"]] ; findbugs plugin
                [["com.google.code.findbugs" "jsr305" "2.0.0"]] ; findbugs plugin
                [["com.puppycrawl.tools" "checkstyle" "5.5"]] ; checkstyle plugin
                [["org.soulspace.clj" "CljModelGenerator" "0.4.0"]] ; mdsd/architecture plugins
                ]
 :dependency-excludes [["ch.qos.logback"]
                       ["avalon-framework"]
                       ["com.ibm.icu"]]
 :log-level :info
 ]