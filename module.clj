[
 :module "BaumeisterAspectJPlugin"
 :project "org.soulspace.baumeister"
 :type :library
 :version "0.5.1"
 :description "AspectJ compiler plugin."
 :plugins ["global" "dependencies" "clojure" "clojuretest" "package"]
 :dependencies [["org.clojure/clojure, 1.5.1"]
                ["org.soulspace.clj/CljXmlLibrary, 0.3.0"]
                ["org.soulspace.baumeister, Baumeister, 0.6.0"]
                ["org.aspectj/aspectjrt, 1.6.11"]
                ["org.aspectj/aspectjtools, 1.6.11"]
                ]
 ]
