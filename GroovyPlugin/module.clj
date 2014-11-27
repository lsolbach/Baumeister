[
 :module "GroovyPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.1.1"
 :description "Groovy compiler plugin for the Baumeister build system."
 :log-level :debug
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.3" :dev]
                ["org.codehaus.groovy/groovy-all, 2.2.0"]]
 ]