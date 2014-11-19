[
 :module "DistributionPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.2"
 :description "Distribution plugin to distribute the built artifacts with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.0" :dev]]
 ]
