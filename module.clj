[
 :module "BaumeisterFindbugsPlugin"
 :project "org.soulspace.baumeister"
 :type :library
 :version "0.5.1"
 :description ""
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package"]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.baumeister/Baumeister, 0.6.0"]
                ["org.soulspace.clj/CljXmlLibrary, 0.3.0"]
                ; add additional findbugs dependencies (findbugs, findbugs-ant, ...)
                ["com.google.code.findbugs/findbugs-ant, 2.0.0"] ; findbugs plugin
                ["com.google.code.findbugs/jsr305, 2.0.0"]] ; findbugs plugin
 ]
