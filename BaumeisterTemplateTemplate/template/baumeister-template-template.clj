[
 {:type :file
  :name "module.clj"
  :content "[:module \"${module}\"
 :project \"${project}\"
 :type :baumeister-template
 :version \"0.1.0\"
 :description \"Baumeister template for creating new projects.\"
 :plugins [[\"org.soulspace.baumeister/PackagePlugin\"]]
 :dependencies []
]"}
 {:type :file
  :name "README.md"
  :content "${module}
==========

Copyright
---------

License
-------
[Eclipse Public License 1.0] (http://www.eclipse.org/legal/epl-v10.html \"EPL 1.0\")
"}
 {:type :file
 :name ".project"
 :content "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<projectDescription>
	<name>${module}</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
	</buildSpec>
	<natures>
	</natures>
</projectDescription>"}
 {:type :file
  :name ".classpath"
  :content "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<classpath>
	<classpathentry kind=\"src\" path=\"src\"/>
	<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/dev/clojure.jar\"/>
	<classpathentry kind=\"lib\" path=\"build/lib/dev/Baumeister.jar\"/>
	<classpathentry kind=\"output\" path=\"bin\"/>
</classpath>
"}
 {:type :file
  :name ".gitignore"
  :content "/bin/
/build/"}
 {:type :directory
  :name "template"}
 ]