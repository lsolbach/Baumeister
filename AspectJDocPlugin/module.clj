[
 :module "AspectJDocPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.1"
 :description "AspectJDoc plugin for the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.1" :dev]
                ["org.aspectj/aspectjrt, 1.6.11"]
                ["org.aspectj/aspectjtools, 1.6.11" :runtime ["org.apache.ant"]]]
 ]
