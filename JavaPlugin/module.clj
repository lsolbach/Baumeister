[
 :module "JavaPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "Java compiler plugin for the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.5" :dev]]
 ]
