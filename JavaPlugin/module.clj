[
 :module "JavaPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.1"
 :description "Java compiler plugin for the Baumeister build system."
 :log-level :debug
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.1" :dev]]
 ]
