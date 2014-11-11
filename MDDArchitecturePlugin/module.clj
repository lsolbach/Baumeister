[
 :module "MDDArchitecturePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.0"
 :description "MDDArchitecture plugin for generating artifacts from UML/XMI models with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.0" :dev]
                ["org.soulspace.clj/CljModelGenerator, 0.5.0"]]
 ]
