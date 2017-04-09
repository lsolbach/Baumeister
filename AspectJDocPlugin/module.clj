[
 :module "AspectJDocPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "AspectJDoc plugin for the Baumeister build system."
 :plugins [["org.soulspace.baumeister/ClojurePlugin, 0.6.5"]
           ["org.soulspace.baumeister/PackagePlugin, 0.6.7"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.9" :dev]
                ["org.aspectj/aspectjrt, 1.8.7"]
                ["org.aspectj/aspectjtools, 1.8.7" :runtime ["org.apache.ant"]]]
 ]
