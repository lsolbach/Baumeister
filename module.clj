[
 :module "ClojureTestPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.0"
 :description "ClojureTest plugin for unit testing clojure code with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.0" :dev]]
 ]
