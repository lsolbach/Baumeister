[
 :module "ClojureDocPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "ClojureDoc plugin for the Baumeister build system"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]] ; add autodoc if used, e.g. ["autodoc/autodoc, 0.9.0"]
 ]
