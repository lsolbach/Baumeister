[
 :module "BaumeisterJUnitPlugin"
 :project "org.soulspace.baumeister"
 :type :library
 :version "0.5.1"
 :description ""
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package"]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.baumeister" "Baumeister" "0.6.0"]
                ["org.soulspace.clj/CljXmlLibrary, 0.3.0"]
                ["org.apache.ant/ant-junit, 1.8.3"] ; junit plugin
                ]
 ]
