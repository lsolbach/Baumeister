[:module "ClojureRingPlugin"
 :project "${project}"
 :type :baumeister-plugin
 :version "0.1.0"
 :description ""
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]]
]