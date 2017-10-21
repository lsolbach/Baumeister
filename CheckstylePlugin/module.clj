[
 :module "CheckstylePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "CheckStyle statical code analysis plugin for the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["com.puppycrawl.tools/checkstyle, 5.5"]]
 ]
