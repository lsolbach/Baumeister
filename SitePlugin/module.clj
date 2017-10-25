[
 :module "SitePlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "Site generation plugin for the generation of module and project web sites with the Baumeister build system"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.soulspace.clj/CljXHtmlLibrary, 0.2.2"]]
 :log-level :trace
 :message-level :finer
 ]
