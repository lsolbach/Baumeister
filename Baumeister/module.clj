[
 :module "Baumeister"
 :project "org.soulspace.baumeister"
 :project-lead "Ludger Solbach"
 :description "Baumeister Build System"
 :type :consolefrontend
 :version "0.6.6"
 :author "Ludger Solbach"
 :provider "soulspace.org"
 :inception-year 2012
 :license ["Eclipse Public License 1.0" "http://www.eclipse.org/legal/epl-v10.html"]
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/EclipsePlugin"]
           ;["org.soulspace.baumeister/MavenPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]
           ["org.soulspace.baumeister/DistributionPlugin"]
           ]
 ;:repository-root-dir "/home/soulman/devel/repositories" ; FIXME use $HOME here
 :dependencies [["org.clojure/clojure, 1.7.0"]
                ["org.soulspace.clj/CljApplicationLibrary, 0.5.2"]
                ["org.soulspace.clj/CljArtifactLibrary, 0.4.4"]
                ["org.soulspace.clj/CljMavenLibrary, 0.5.2"]
                ["org.soulspace.clj/CljJavaLibrary, 0.6.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.4.1"] ; xml generation, maven pom, eclipse
                ["org.clojure/data.zip, 0.1.1"] ; maven pom support
                ["org.clojure/tools.reader, 0.8.3"] ; edn reader
                ["org.apache.ant/ant-junit, 1.8.3"]]
 :dependency-excludes [["ch.qos.logback"]
                       ["avalon-framework"]
                       ["com.ibm.icu"]]
 ]
