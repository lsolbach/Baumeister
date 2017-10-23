[
 :module "JythonPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "Jython plugin for the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.python/jython, 2.7.0" :runtime ["org.apache.ant"]]]
 ]
