[
 :module "ClojurePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.0"
 :description "Compiler plugin for Clojure"
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.1" :dev]]
 ]
