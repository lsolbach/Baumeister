[
 :module "PackagePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.7"
 :description "Package plugin to package the built artifacts with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]]
 ]
