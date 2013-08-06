[
 :project "project"
 :module "v"
 :version "1.0.0"

 :parent ["project" "u" "1.0.0"] ; parent artifact coordinates
 :modules ["x" "y" "z"]
 
 :description ""
 :homepage []
 
 :licenses []
 :provider []
 :author []
 :project-lead []
 :developers []
 :contributors []

 :plugins ["a" ["project.b" "b" "1.2.0"] "c" ["project.d" "d"]]

 :dependencies []
 
 :distribution-packages [{:name "${module}-{version}" :type "zip" :sources []}]
 :distribution-targets []
 ]