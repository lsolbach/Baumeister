[
 :module "JDependPlugin"
 :project "org.soulspace.baumeister"
 :type :baumeister-plugin
 :version "0.7.0"
 :description "JDepend statical code analysis plugin for the Baumeister build system"
 :plugins [["org.soulspace.baumeister/ClojurePlugin"]
           ["org.soulspace.baumeister/PackagePlugin"]]
 :dependencies [["org.soulspace.baumeister/Baumeister" :dev]
                ["org.apache.ant/ant-jdepend, 1.8.3" :runtime ["org.apache.ant"]]
                ["jdepend/jdepend, 2.9.1"]]
 ]
