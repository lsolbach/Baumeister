[
 {:file "module.clj"
  :content "[:module \"${module}\"
 :project \"${project}\"
 :type :${type}
 :version \"0.1.0\"
 :description ""
 :plugins [[\"org.soulspace.baumeister/DependencyPlugin\"]
           [\"org.soulspace.baumeister/ClojurePlugin\"]
           [\"org.soulspace.baumeister/PackagePlugin\"]]
 :dependencies [[\"org.clojure/clojure, 1.7.0\"]]
]"}
 {:file "README.md"
  :content "${module}
==========

Copyright
---------

License
-------

"}
 {:file ".project"
  :content "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<projectDescription>
	<name>${module}</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>ccw.builder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
		<nature>ccw.nature</nature>
	</natures>
</projectDescription>"}
 {:file ".classpath"
  :content "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<classpath>
	<classpathentry kind=\"src\" path=\"src\"/>
	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/runtime/clojure.jar\"/>
	<classpathentry kind=\"output\" path=\"bin\"/>
</classpath>
"}
 {:file ".gitignore"
  :content "/bin/
/build/"}
 {:dir "src"}
 ]
