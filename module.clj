[
 :module "BaumeisterSitePlugin"
 :project "org.soulspace.baumeister"
 :type :library
 :version "0.5.1"
 :description "The BaumeisterSitePlugin is a plugin for the Baumeister build system that creates web sites with information about modules and projects."
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package"]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.baumeister/Baumeister, 0.6.0"]
                ["org.soulspace.clj/CljXHtmlLibrary, 0.2.0"]]
 ]
