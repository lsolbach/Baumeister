[
 :module "Baumeister"
 :project "org.soulspace.baumeister"
 :project-lead "Ludger Solbach"
 :description "Baumeister Build System"
 :type :consolefrontend
 :version "0.6.0"
 :author "Ludger Solbach"
 :provider "soulspace.org"
 :inception-year 2012
 :plugins ["global" "dependencies" "clojure" "package" "release" "bmdebug"] ; "eclipse" "clojuretest"
 :plugins-test [["baumeister.plugin/eclipse, 0.5.1"]
                ["baumeister.plugin/maven, 0.5.1"]
                ["baumeister.plugin/pmd, 0.5.1"]]
 ; ["org.soulspace.baumeister.plugin" "maven" "0.5.1"]
  :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :repository-root-dir "/home/soulman/devel/repositories" ; FIXME use $HOME here
 ; :extra-repositories [[:maven-proxy :thirdparty "http://maven.alfresco.com/nexus/content/groups/public" "${baumeister-home-dir}/../repositories/maven.alfresco.com"]]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.clj/CljApplicationLibrary, 0.5.1"]
                ["org.soulspace.clj/CljArtifactLibrary, 0.4.3"]
                ["org.soulspace.clj/CljMavenLibrary, 0.5.1"]
                ["org.soulspace.clj/CljJavaLibrary, 0.3.0"]
                ["org.tcrawley/dynapath, 0.2.3"] ; dynamic classpath support
                ["org.soulspace.clj/CljXmlLibrary, 0.3.0"] ; xml generation, maven, eclipse
                ["org.clojure/data.zip, 0.1.1"] ; maven support
                ;["org.clojure/tools.cli, 0.2.2"] ; command line arguments
                ["org.clojure/tools.nrepl, 0.2.3"] ; repl support
                ["org.apache.ant/ant, 1.8.3"]
                ["org.apache.ant/ant-launcher, 1.8.3"]
                ["org.apache.ant/ant-junit, 1.8.3"]
                ["org.aspectj/aspectjrt, 1.6.11"] ; aspectj plugin
                ["org.soulspace.clj/CljModelGenerator, 0.4.0"] ; mdsd/architecture plugins
                ]
 :dependency-excludes [["ch.qos.logback"]
                       ["avalon-framework"]
                       ["com.ibm.icu"]]
 :log-level :info
 ]