[
 :module "GroovyPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "Groovy compiler plugin for the Baumeister build system"
 :log-level :debug
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.codehaus.groovy/groovy-all, 2.2.0"]]
 ]
