[
 :module "MavenPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.0"
 :description "Maven plugin for the integration of the Maven build system and the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.0" :dev]]
 ]