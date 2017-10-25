[
 :module "MDDArchitecturePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "MDDArchitecture plugin for generating artifacts from UML/XMI models with the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin, 0.6.5"]
           ["org.soulspace.baumeister/PackagePlugin, 0.6.5"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.7.0" :dev]
                ["org.soulspace.clj/CljModelGenerator, 0.5.5"]]
 ]
