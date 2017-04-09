[
 :module "PackagePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.8"
 :description "Package plugin to package the built artifacts with the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin, 0.6.5"]
           ["org.soulspace.baumeister/PackagePlugin, 0.6.5"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.9" :dev]]
 ]
