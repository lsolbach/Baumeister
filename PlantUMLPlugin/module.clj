[
 :module "PlantUMLPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.0"
 :description "Plugin to generate UML diagrams from text files with PlantUML files."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.0" :dev]
                ["net.sourceforge.plantuml/plantuml, 7991" :runtime ["org.apache.ant"]]]
 ]