[
 :module "EclipsePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "Plugin for the integration of the Baumeister builds with Eclipse."
 :plugins [["org.soulspace.baumeister/ClojurePlugin, 0.6.5"]
           ["org.soulspace.baumeister/PackagePlugin, 0.6.7"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.9" :dev]
                ["org.soulspace.clj/CljEclipseLibrary, 0.1.1"]]
 ]
