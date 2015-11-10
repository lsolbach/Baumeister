[
 {:file "module.clj"
  :content "[:module \"${module}\"
 :project \"${project}\"
 :type :${type}
 :version \"0.1.0\"
 :description ""
 :plugins [[\"org.soulspace.baumeister/DependencyPlugin\"]
           [\"org.soulspace.baumeister/AspectJPlugin\"]
           [\"org.soulspace.baumeister/JUnitPlugin\"]
           [\"org.soulspace.baumeister/PackagePlugin\"]]
 :dependencies [[\"org.aspectj/aspectjrt, 1.8.7\"]
                [\"junit/junit\" :dev]]
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
			<name>org.eclipse.ajdt.core.ajbuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.ajdt.ui.ajnature</nature>
		<nature>org.eclipse.jdt.core.javanature</nature>
	</natures>
</projectDescription>"}
 {:file ".classpath"
  :content "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<classpath>
	<classpathentry kind=\"src\" path=\"src\"/>
	<classpathentry kind=\"src\" path=\"unittest\"/>
	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>
	<classpathentry kind=\"con\" path=\"org.eclipse.ajdt.core.ASPECTJRT_CONTAINER\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/dev/junit.jar\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/runtime/aspectjrt.jar\"/>
	<classpathentry kind=\"output\" path=\"bin\"/>
</classpath>
"}
 {:file ".gitignore"
  :content "/bin/
/build/"}
 {:dir "src"}
 {:dir "unittest"}
 ]
