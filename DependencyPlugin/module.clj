[
 :module "DependencyPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "Plugin for resolving transitive dependencies with Baumeister."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.5" :dev]]
 ]
