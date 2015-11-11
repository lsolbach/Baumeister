[
 :module "GenesisPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "Plugin for the creation of new modules with the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.soulspace.baumeister/AspectJTemplate, 0.1.0, AspectJTemplate, zip" :data]
                ["org.soulspace.baumeister/ClojureTemplate, 0.1.0, ClojureTemplate, zip" :data]
                ["org.soulspace.baumeister/DataTemplate, 0.1.0, DataTemplate, zip" :data]
                ["org.soulspace.baumeister/JavaTemplate, 0.1.0, JavaTemplate, zip" :data]]
 ]
