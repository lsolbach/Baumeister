[
 {:type :file
  :name "module.clj"
  :content "[:module \"${module}\"
 :project \"${project}\"
 :type :baumeister-plugin
 :version \"0.1.0\"
 :description \"\"
 :plugins [[\"org.soulspace.baumeister/ClojurePlugin\"]
           [\"org.soulspace.baumeister/PackagePlugin\"]]
 :dependencies [[\"org.soulspace.baumeister/Baumeister\" :dev]]
]"}
 {:type :file
  :name "README.md"
  :content "${module} for the Baumeister GenesisPlugin 
======================================================

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
 {:type :file
  :name ".gitignore"
  :content "/bin/
/build/"}
 {:type :directory
  :name "src/baumeister/plugin"}
 ]