[
 :module "FindbugsPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "FindBugs statical code analysis plugin for the Baumeister build system"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["com.google.code.findbugs/findbugs-ant, 2.0.0" :runtime ["org.apache.ant"]]]
 ]
