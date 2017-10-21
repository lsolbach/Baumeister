[
 :module "CoberturaPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "Cobertura plugin for measuring the code coverage with the Baumeister build system"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["net.sourceforge.cobertura/cobertura, 1.9.4.1" :runtime ["org.apache.ant"]]]
 ]
