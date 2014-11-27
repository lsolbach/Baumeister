[
 :module "EclipsePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.3"
 :description "Plugin for the integration of the Baumeister builds with Eclipse."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.3" :dev]]
 ]
