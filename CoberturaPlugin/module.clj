[
 :module "CoberturaPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "Cobertura plugin for the measure code coverage with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.5" :dev]
                ["net.sourceforge.cobertura/cobertura, 1.9.4.1" :runtime ["org.apache.ant"]]]
 ]
