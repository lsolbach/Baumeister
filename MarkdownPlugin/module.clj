[
 :module "MarkdownPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "Markdown plugin to generate markdown files with the Baumeister build system"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.soulspace.clj/CljMarkdownLibrary, 0.2.0"]] ; TODO exclude clojure here, it's a transitive :dev dep
 ]
