[
 :module "ClojureDocPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.1"
 :description "ClojureDoc plugin for the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.1" :dev]] ; add autodoc if used, e.g. ["autodoc/autodoc, 0.9.0"]
 ]