[
 :module "JDependPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.6.5"
 :description "JDepend statical code analysis plugin for the Baumeister build system."
 :plugins ["global"
           ["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister, 0.6.5" :dev]
                ["org.apache.ant/ant-jdepend, 1.8.3" :runtime ["org.apache.ant"]]
                ["jdepend/jdepend, 2.9.1"]]
 ]
