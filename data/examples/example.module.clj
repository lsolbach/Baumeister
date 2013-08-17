[
 :project "org.project"
 :module "ModuleA"
 :version "1.0.0"

 :parent ["org.project" "parent" "1.0.0"] ; parent artifact coordinates
 :modules ["ModuleX" "ModuleY" "ModuleZ"]
 
 :description ""
 :homepage []
 
 :licenses [["" ""]]
 :provider []
 :author []
 :project-lead ["Mister X"]
 :developers ["Joe Doe"]
 :contributors ["Tommy Gun"]

 :plugins ["a" ["org.soulspace.baumeister" "BaumeisterClojurePlugin" "1.2.0"] "c" ["org.soulspace.baumeister" "BaumeisterClojureTestPlugin"]]

 :dependencies [["org.apache.log4j/log4j, 1.2.15"]]
 
 :distribution-packages [{:name "${module}-{version}" :type "zip" :sources []}]
 :distribution-targets []
 ]