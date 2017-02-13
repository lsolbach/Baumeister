[
 :module "MDDGeneratorPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.6"
 :description "MDDGenerator plugin for generating artifacts from UML/XMI models with the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.soulspace.clj/CljModelGenerator, 0.5.2"]]
 ]
