[
 :module "MarkdownPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.0"
 :description "Markdown plugin to generate markdown files with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/DependencyPlugin"]
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.0" :dev]
                ["org.soulspace.clj/CljMarkdownLibrary, 0.1.0"]] ; TODO exclude clojure here, it's a transitive :dev dep
 ]
