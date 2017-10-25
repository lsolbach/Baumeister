[
 :module "PMDPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "PMD statical code analysis plugin for the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["net.sourceforge.pmd/pmd, 5.0.0" :runtime ["org.apache.ant"]]]
 ]
