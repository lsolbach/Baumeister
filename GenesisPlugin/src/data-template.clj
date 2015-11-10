[
 {:type :file
  :name ".project"
  :content "[:module \"${module}\"
 :project \"${project}\"
 :type :data
 :version \"0.1.0\"
 :description \"\"
 :plugins [[\"org.soulspace.baumeister/DependencyPlugin\"]
           [\"org.soulspace.baumeister/PackagePlugin\"]]
 :dependencies [[\"org.clojure/clojure, 1.7.0\"]]
]"}
 {:type :file
  :name "README.md"
  :content "${module}
==========

Copyright
---------

License
-------

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
  :name ".gitignore"
  :content "/bin/
/build/"}
 {:type :directory
  :name "data"}
 ]