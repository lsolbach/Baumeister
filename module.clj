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
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :repository-root-dir "/home/soulman/devel/repositories" ; FIXME use $HOME here
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.clj/CljApplicationLibrary, 0.5.1"]
                ["org.soulspace.clj/CljArtifactLibrary, 0.4.3"]
                ["org.soulspace.clj/CljMavenLibrary, 0.5.1"]
                ["org.soulspace.clj/CljJavaLibrary, 0.4.0"]
                ["org.soulspace.clj/CljXmlLibrary, 0.4.0"] ; xml generation, maven, eclipse
                ["org.clojure/data.zip, 0.1.1"] ; maven support
                ["org.clojure/tools.nrepl, 0.2.3"] ; repl support
                ["org.apache.ant/ant, 1.8.3"]
                ["org.apache.ant/ant-launcher, 1.8.3"]
                ["org.apache.ant/ant-junit, 1.8.3"]
                ["org.soulspace.clj/CljModelGenerator, 0.4.0"]] ; mdsd/architecture plugins
 :dependency-excludes [["ch.qos.logback"]
                       ["avalon-framework"]
                       ["com.ibm.icu"]]
 :log-level :info
 ]
