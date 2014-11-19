[
 :project "org.project"
 :module "ModuleA"
 :version "1.0.0"
 :parent ["org.project" "parent" "1.0.0"] ; parent artifact coordinates
 ; not yet :modules ["ModuleX" "ModuleY" "ModuleZ"]
 :description ""
 :homepage []
 :licenses [["" ""]]
 :provider []
 :author []
 :project-lead ["Mister X"]
 :developers ["Joe Doe"]
 :contributors ["Tommy Gun"]
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin, 1.2.0"]
           ["org.soulspace.baumeister/ClojureTestPlugin"]]
 :dependencies [["org.apache.log4j/log4j, 1.2.15"]]
 :distribution-packages [{:name "${module}-{version}" :type "zip" :sources []}]
 :distribution-targets []
 ]