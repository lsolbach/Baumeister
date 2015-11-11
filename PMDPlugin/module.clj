[
 :module "PMDPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "PMD statical code analysis plugin for the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.5" :dev]
                ["net.sourceforge.pmd/pmd, 5.0.0" :runtime ["org.apache.ant"]]]
 ]
