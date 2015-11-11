[
 :module "AspectJDocPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "AspectJDoc plugin for the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.aspectj/aspectjrt, 1.8.7"]
                ["org.aspectj/aspectjtools, 1.8.7" :runtime ["org.apache.ant"]]]
 ]
