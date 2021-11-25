[
 :module "PlantUMLPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "Plugin to generate UML diagrams from text files with PlantUML files."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["net.sourceforge.plantuml/plantuml, 7991" :runtime ["org.apache.ant"]]]]
